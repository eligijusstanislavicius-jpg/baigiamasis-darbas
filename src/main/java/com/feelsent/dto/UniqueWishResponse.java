package com.feelsent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UniqueWishResponse {

    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private List<AssignmentInfo> assignments;

    @Data
    @AllArgsConstructor
    public static class AssignmentInfo {
        private Long userUniqueWishId;
        private Long userId;
        private String firstName;
        private String lastName;
        private LocalDateTime expiresAt;
        private LocalDateTime assignedAt;
    }
}
