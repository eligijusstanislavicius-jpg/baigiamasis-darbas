package com.feelsent.model;

import com.feelsent.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user_read", columnList = "user_id, is_read"),
    @Index(name = "idx_notification_user_created", columnList = "user_id, created_at")
})
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user; // pranešimo gavėjas

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String text; // žmogiškai skaitomas pranešimo tekstas

    @Column(nullable = false)
    private boolean isRead = false;

    private Long relatedEntityId; // susijusio objekto ID (žinutės, draugystės)

    @Column(nullable = false)
    private LocalDateTime createdAt;
}