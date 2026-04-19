package com.feelsent.dto;

import com.feelsent.enums.SendMode;
import lombok.Data;

// Duomenys žinutei siųsti
@Data
public class SendMessageRequest {
    private Long receiverId;
    private Long wishId;       // palinkėjimo ID iš DB (gali būti iš pasiūlymų arba iš mėgstamų)
    private SendMode sendMode;
}
