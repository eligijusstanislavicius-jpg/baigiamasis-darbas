package com.feelsent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserUniqueWishResponse {

    private Long id;           // UserUniqueWish id (naudojamas šalinimui)
    private Long uniqueWishId;
    private String text;
    private LocalDateTime expiresAt;   // null = galioja kol vartotojas ištrina
    private LocalDateTime assignedAt;
}
