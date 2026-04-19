package com.feelsent.exception;

// Išmetama kai vartotojas bando pridėti daugiau nei 10 asmeninių palinkėjimų – grąžina HTTP 400
public class FavoriteWishLimitException extends RuntimeException {

    public FavoriteWishLimitException(String message) {
        super(message);
    }
}
