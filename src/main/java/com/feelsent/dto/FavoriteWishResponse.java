package com.feelsent.dto;

import com.feelsent.enums.WishTone;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FavoriteWishResponse {
    private Long id;
    private Long wishId;
    private String text;
    private WishTone tone;
    private String toneLabel;
    private String imageUrl;
    private LocalDateTime createdAt;
}
