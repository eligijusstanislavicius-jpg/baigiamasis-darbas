package com.feelsent.service;

import com.feelsent.dto.NotificationResponse;
import com.feelsent.enums.NotificationType;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.Notification;
import com.feelsent.model.User;
import com.feelsent.repository.NotificationRepository;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Sukuria in-app pranešimą vartotojui
    public void create(User user, NotificationType type, String text, Long relatedEntityId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setText(text);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // Grąžina visus vartotojo pranešimus (naujausias pirma)
    public List<NotificationResponse> getAll(String email) {
        User user = getUser(email);
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Pažymi vieną pranešimą kaip perskaitytą
    public void markRead(String email, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Pranešimas nerastas"));

        if (!notification.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Neturite teisės keisti šio pranešimo");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // Pažymi visus vartotojo pranešimus kaip perskaitytus – viena SQL UPDATE užklausa
    @Transactional
    public void markAllRead(String email) {
        User user = getUser(email);
        notificationRepository.markAllReadByUser(user);
    }

    // Naudojama re-engagement logikoje – tikrina ar yra neperskaitytų
    public boolean hasUnread(User user) {
        return notificationRepository.existsByUserAndIsReadFalse(user);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getText(),
                n.isRead(),
                n.getRelatedEntityId(),
                n.getCreatedAt()
        );
    }
}