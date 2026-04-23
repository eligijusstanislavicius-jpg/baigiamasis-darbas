package com.feelsent.service;

import com.feelsent.dto.MessageLimitResponse;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.MessageLimit;
import com.feelsent.model.User;
import com.feelsent.repository.MessageLimitRepository;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageLimitService {

    private final MessageLimitRepository messageLimitRepository;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;

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

        // Jei limitas jau yra – atnaujiname, jei ne – kuriame naują
        MessageLimit limit = messageLimitRepository.findByReceiverAndSender(receiver, sender)
                .orElseGet(() -> {
                    MessageLimit newLimit = new MessageLimit();
                    newLimit.setReceiver(receiver);
                    newLimit.setSender(sender);
                    return newLimit;
                });

        limit.setDailyLimit(dailyLimit);
        return toResponse(messageLimitRepository.save(limit));
    }

    // Gavėjas pašalina limitą – siuntėjas vėl gali siųsti neribotai
    @Transactional
    public void removeLimit(String receiverEmail, Long senderId) {
        User receiver = getUser(receiverEmail);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Siuntėjas nerastas"));

        MessageLimit limit = messageLimitRepository.findByReceiverAndSender(receiver, sender)
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

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
    }

    private MessageLimitResponse toResponse(MessageLimit ml) {
        return new MessageLimitResponse(
                ml.getId(),
                ml.getSender().getId(),
                ml.getSender().getUsername(),
                ml.getDailyLimit()
        );
    }
}