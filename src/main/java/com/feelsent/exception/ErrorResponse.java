package com.feelsent.exception;

import java.time.LocalDateTime;

// Objektas kuris grąžinamas klientui kai įvyksta klaida
// Pvz.: { "code": "USER_NOT_FOUND", "message": "Vartotojas nerastas", "timestamp": "2026-04-08T10:00:00" }
public class ErrorResponse {

    private String code;       // klaidos kodas (trumpas)
    private String message;    // klaidos aprašymas
    private LocalDateTime timestamp; // kada įvyko klaida

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
