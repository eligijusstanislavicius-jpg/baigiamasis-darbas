package com.feelsent.service;

import com.feelsent.dto.MessageLimitResponse;
import com.feelsent.enums.NotificationType;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.MessageLimit;
import com.feelsent.model.User;
import com.feelsent.repository.MessageLimitRepository;
import com.feelsent.repository.MessageRepository;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageLimitService {

    private final MessageLimitRepository messageLimitRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;
    private final NotificationService notificationService;

    // Gavėjas nustato arba atnaujina limitą konkrečiam siuntėjui
    // Jei limitas jau egzistuoja – atnaujinamas, jei ne – sukuriamas naujas
    @Transactional
    public MessageLimitResponse setLimit(String receiverEmail, Long senderId, int dailyLimit) {
        if (dailyLimit < 1) {
            throw new IllegalArgumentException("Limitas turi būti bent 1");
        }

        User receiver = getUser(receiverEmail);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Siuntėjas nerastas"));

        // Limitą galima nustatyti tik draugams
        if (!friendshipService.areFriends(receiver, sender)) {
            throw new IllegalArgumentException("Limitą galima nustatyti tik draugams");
        }

        boolean isNew = !messageLimitRepository.existsByReceiverAndSender(receiver, sender);

        MessageLimit limit = messageLimitRepository.findFirstByReceiverAndSender(receiver, sender)
                .orElseGet(() -> {
                    MessageLimit newLimit = new MessageLimit();
                    newLimit.setReceiver(receiver);
                    newLimit.setSender(sender);
                    return newLimit;
                });

        limit.setDailyLimit(dailyLimit);
        MessageLimitResponse response = toResponse(messageLimitRepository.save(limit));

        if (isNew) {
            notificationService.create(
                    sender,
                    NotificationType.MESSAGE_LIMIT_SET,
                    receiver.getFirstName() + " apribojo jūsų žinučių siuntimą iki " + dailyLimit + " per parą",
                    null
            );
        }

        return response;
    }

    // Gavėjas pašalina limitą – siuntėjas vėl gali siųsti neribotai
    @Transactional
    public void removeLimit(String receiverEmail, Long senderId) {
        User receiver = getUser(receiverEmail);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Siuntėjas nerastas"));

        MessageLimit limit = messageLimitRepository.findFirstByReceiverAndSender(receiver, sender)
                .orElseThrow(() -> new IllegalArgumentException("Limitas nerastas"));

        messageLimitRepository.delete(limit);
    }

    // Grąžina visus šio gavėjo nustatytus limitus (kieno siuntimą apribojau)
    public List<MessageLimitResponse> getMyLimits(String receiverEmail) {
        User receiver = getUser(receiverEmail);
        return messageLimitRepository.findAllByReceiver(receiver)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Grąžina limito informaciją siuntėjui — kiek žinučių liko šiam gavėjui per 24 val.
    public Map<String, Object> getLimitInfo(String senderEmail, Long receiverId) {
        User sender = getUser(senderEmail);
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        return messageLimitRepository.findFirstByReceiverAndSender(receiver, sender)
                .map(limit -> {
                    long sentToday = messageRepository.countBySenderAndReceiverAndSentAtAfter(
                            sender, receiver, LocalDateTime.now().minusHours(24));
                    long remaining = Math.max(0, limit.getDailyLimit() - sentToday);
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("limited", true);
                    result.put("dailyLimit", limit.getDailyLimit());
                    result.put("sentToday", sentToday);
                    result.put("remaining", remaining);
                    return result;
                })
                .orElseGet(() -> {
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("limited", false);
                    return result;
                });
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
    }

    private MessageLimitResponse toResponse(MessageLimit ml) {
        return new MessageLimitResponse(
                ml.getId(),
                ml.getSender().getId(),
                ml.getSender().getFirstName(),
                ml.getDailyLimit()
        );
    }
}