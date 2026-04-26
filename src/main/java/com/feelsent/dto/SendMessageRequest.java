package com.feelsent.dto;

import com.feelsent.enums.SendMode;
import lombok.Data;

// Duomenys žinutei siųsti
@Data
public class SendMessageRequest {
    private Long receiverId;
    private Long wishId;         // bendras palinkėjimas (arba šis, arba uniqueWishId)
    private Long uniqueWishId;   // asmeninis palinkėjimas
    private SendMode sendMode;
}
