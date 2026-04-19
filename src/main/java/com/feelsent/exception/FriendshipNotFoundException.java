package com.feelsent.exception;

// Išmetama kai draugystė nerasta DB – grąžina HTTP 404
public class FriendshipNotFoundException extends RuntimeException {

    public FriendshipNotFoundException(String message) {
        super(message);
    }
}
