package com.feelsent.exception;

// Išmetama kai vartotojas nerastas DB – grąžina HTTP 404
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
