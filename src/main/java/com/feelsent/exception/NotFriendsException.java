package com.feelsent.exception;

// Išmetama kai bandoma siųsti žinutę ne draugui – grąžina HTTP 403
public class NotFriendsException extends RuntimeException {

    public NotFriendsException(String message) {
        super(message);
    }
}
