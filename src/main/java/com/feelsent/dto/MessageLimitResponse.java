package com.feelsent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Limito duomenys grąžinami klientui
@Data
@AllArgsConstructor
public class MessageLimitResponse {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private Integer dailyLimit; // kiek žinučių per dieną leidžiama siųsti
}