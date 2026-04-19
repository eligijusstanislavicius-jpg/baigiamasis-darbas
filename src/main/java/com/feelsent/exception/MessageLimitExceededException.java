package com.feelsent.exception;

// Išmetama kai siuntėjas viršija gavėjo nustatytą dienos limitą – grąžina HTTP 429
public class MessageLimitExceededException extends RuntimeException {

    public MessageLimitExceededException(String message) {
        super(message);
    }
}
