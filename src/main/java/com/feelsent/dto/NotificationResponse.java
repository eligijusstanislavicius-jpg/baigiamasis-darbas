package com.feelsent.dto;

import com.feelsent.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String text;
    private boolean isRead;
    private Long relatedEntityId;
    private LocalDateTime createdAt;
}