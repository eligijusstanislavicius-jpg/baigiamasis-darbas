package com.feelsent.service;

import com.feelsent.dto.MessageResponse;
import com.feelsent.dto.SendMessageRequest;
import com.feelsent.enums.MessageStatus;
import com.feelsent.enums.NotificationType;
import com.feelsent.enums.Reaction;
import com.feelsent.enums.SendMode;
import com.feelsent.enums.WishTone;
import com.feelsent.exception.MessageLimitExceededException;
import com.feelsent.exception.NotFriendsException;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.*;
import com.feelsent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WishRepository wishRepository;
    private final MessageLimitRepository messageLimitRepository;
    private final FriendshipService friendshipService;
    private final PointService pointService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    // Siunčia žinutę – pagrindinė logika
    @Transactional
    public MessageResponse send(String senderEmail, SendMessageRequest request) {
        User sender = getUser(senderEmail);
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new UserNotFoundException("Gavėjas nerastas"));

        // Verslo taisyklė: siųsti galima TIK tarp ACCEPTED draugų
        if (!friendshipService.areFriends(sender, receiver)) {
            throw new NotFriendsException("Galima siųsti tik draugams");
        }

        // Tikrinama žinutės limito taisyklė (gavėjas galėjo apriboti siuntėją)
        checkDailyLimit(sender, receiver);

        if (request.getWishId() == null) {
            throw new IllegalArgumentException("Reikia nurodyti palinkėjimą (wishId)");
        }

        Wish wish = wishRepository.findById(request.getWishId())
                .orElseThrow(() -> new IllegalArgumentException("Palinkėjimas nerastas"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setWish(wish);
        message.setSendMode(request.getSendMode());
        message.setStatus(MessageStatus.SENT);
        message.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        // Pranešame gavėjui apie naują žinutę (in-app)
        notificationService.create(
                receiver,
                NotificationType.NEW_MESSAGE,
                sender.getFirstName() + " atsiuntė jums palinkėjimą!",
                saved.getId()
        );

        return toResponse(saved);
    }

    // Gavėjas atidaro žinutę: SENT → OPENED
    @Transactional
    public MessageResponse openMessage(String receiverEmail, Long messageId) {
        Message message = findMessageForReceiver(receiverEmail, messageId);

        if (message.getStatus() != MessageStatus.SENT) {
            throw new IllegalArgumentException("Žinutė jau atidaryta");
        }

        message.setStatus(MessageStatus.OPENED);
        return toResponse(messageRepository.save(message));
    }

    // Gavėjas spėja toną (tik GUESS režimas): OPENED → GUESSED
    // Jei teisingai – siuntėjas gauna 5 taškus
    @Transactional
    public MessageResponse guessWish(String receiverEmail, Long messageId, WishTone guessedTone) {
        Message message = findMessageForReceiver(receiverEmail, messageId);

        if (message.getSendMode() != SendMode.GUESS) {
            throw new IllegalArgumentException("Spėjimas galimas tik GUESS režimo žinutėms");
        }
        if (message.getStatus() != MessageStatus.OPENED) {
            throw new IllegalArgumentException("Žinutė turi būti atidaryta prieš spėjant");
        }

        boolean correct = message.getWish().getTone() == guessedTone;
        message.setGuessResult(correct);
        message.setStatus(MessageStatus.GUESSED);

        // Jei teisingai atspėjo – siuntėjas gauna 5 taškus
        if (correct) {
            pointService.awardGuessCorrect(message.getSender(), message);
        }

        return toResponse(messageRepository.save(message));
    }

    // Gavėjas reaguoja į žinutę: → REACTED
    // Siuntėjas gauna 10 taškų. Grąžiname suggestReply=true (grandinės efektas)
    @Transactional
    public MessageResponse reactToMessage(String receiverEmail, Long messageId, Reaction reaction) {
        Message message = findMessageForReceiver(receiverEmail, messageId);

        // Galima reaguoti po atidarymo arba po atspėjimo
        if (message.getStatus() != MessageStatus.OPENED && message.getStatus() != MessageStatus.GUESSED) {
            throw new IllegalArgumentException("Žinutė turi būti atidaryta prieš reaguojant");
        }

        message.setReaction(reaction);
        message.setStatus(MessageStatus.REACTED);

        // Siuntėjas gauna 10 taškų už gautą reakciją
        pointService.awardReactionReceived(message.getSender(), message);

        // Pranešame siuntėjui kad gavėjas sureagavo (in-app)
        notificationService.create(
                message.getSender(),
                NotificationType.MESSAGE_REACTED,
                message.getReceiver().getFirstName() + " sureagavo į jūsų palinkėjimą: "
                        + reaction.getEmoji(),
                message.getId()
        );

        return toResponse(messageRepository.save(message));
    }

    private static final List<MessageStatus> INACTIVE_STATUSES =
            Arrays.asList(MessageStatus.REACTED, MessageStatus.EXPIRED);

    // Inbox: tik laukiančios reakcijos žinutės – be REACTED ir EXPIRED
    @Transactional(readOnly = true)
    public List<MessageResponse> getInbox(String email) {
        User user = getUser(email);
        return messageRepository.findAllByReceiverAndStatusNotInOrderBySentAtDesc(user, INACTIVE_STATUSES)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Sent: tik aktyvios žinutės – be REACTED ir EXPIRED
    @Transactional(readOnly = true)
    public List<MessageResponse> getSent(String email) {
        User user = getUser(email);
        return messageRepository.findAllBySenderAndStatusNotInOrderBySentAtDesc(user, INACTIVE_STATUSES)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Tikrina ar siuntėjas neviršijo gavėjo nustatyto dienos limito
    private void checkDailyLimit(User sender, User receiver) {
        messageLimitRepository.findByReceiverAndSender(receiver, sender).ifPresent(limit -> {
            LocalDateTime since = LocalDateTime.now().minusHours(24);
            long sent = messageRepository.countBySenderAndReceiverAndSentAtAfter(sender, receiver, since);
            if (sent >= limit.getDailyLimit()) {
                throw new MessageLimitExceededException(
                        "Pasiektas dienos limitas (" + limit.getDailyLimit() + " žinutės per 24 val.)");
            }
        });
    }

    // Randa žinutę ir patikrina kad šis vartotojas yra gavėjas
    private Message findMessageForReceiver(String receiverEmail, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Žinutė nerasta"));
        if (!message.getReceiver().getEmail().equals(receiverEmail)) {
            throw new IllegalArgumentException("Neturite teisės pasiekti šios žinutės");
        }
        return message;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
    }

    // Paverčia Message entity į DTO
    // GUESS režimas: kol status=SENT arba OPENED – tekstas neparodytas (tik paveikslėlis)
    private MessageResponse toResponse(Message m) {
        boolean isGuessMode = m.getSendMode() == SendMode.GUESS;
        boolean revealed = !isGuessMode
                || m.getStatus() == MessageStatus.GUESSED
                || m.getStatus() == MessageStatus.REACTED;

        // GUESS režimas: tekstas ir tonas slepiami kol gavėjas neatspėja
        String wishText = revealed ? m.getWish().getText() : null;
        WishTone wishTone = revealed ? m.getWish().getTone() : null;
        String wishToneLabel = revealed ? m.getWish().getTone().getLabel() : null;

        boolean suggestReply = m.getStatus() == MessageStatus.REACTED;

        Reaction reaction = m.getReaction();
        return new MessageResponse(
                m.getId(),
                m.getSender().getId(),
                m.getSender().getUsername(),
                m.getSender().getFirstName(),
                m.getReceiver().getId(),
                m.getReceiver().getUsername(),
                m.getReceiver().getFirstName(),
                m.getWish().getId(),
                wishText,
                wishTone,
                wishToneLabel,
                "/static/images/wishes/" + m.getWish().getId() + ".png",
                m.getSendMode(),
                m.getSendMode().getLabel(),
                m.getStatus(),
                m.getGuessResult(),
                reaction,
                reaction != null ? reaction.getLabel() : null,
                reaction != null ? reaction.getEmoji() : null,
                m.getSentAt(),
                suggestReply
        );
    }
}
