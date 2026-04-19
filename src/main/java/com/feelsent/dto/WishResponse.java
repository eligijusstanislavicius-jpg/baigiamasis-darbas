package com.feelsent.dto;

import com.feelsent.enums.WishTone;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WishResponse {
    private Long id;
    private String text;
    private WishTone tone;
    private String toneLabel;
    private String relationshipType;
    private String imageUrl;
}
