package com.feelsent.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice – pagauna visas klaidas iš visų kontrolerių vienoje vietoje
// @Slf4j – Lombok: sukuria log kintamąjį automatiškai (log.error, log.info ir t.t.)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.error("Vartotojas nerastas: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(FriendshipNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFriendshipNotFound(FriendshipNotFoundException ex) {
        log.error("Draugystė nerasta: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(new ErrorResponse("FRIENDSHIP_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(NotFriendsException.class)
    public ResponseEntity<ErrorResponse> handleNotFriends(NotFriendsException ex) {
        log.error("Nėra draugystės: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(new ErrorResponse("NOT_FRIENDS", ex.getMessage()));
    }

    @ExceptionHandler(MessageLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleMessageLimit(MessageLimitExceededException ex) {
        log.error("Žinučių limitas viršytas: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS) // 429
                .body(new ErrorResponse("MESSAGE_LIMIT_EXCEEDED", ex.getMessage()));
    }

    @ExceptionHandler(FavoriteWishLimitException.class)
    public ResponseEntity<ErrorResponse> handleFavoriteWishLimit(FavoriteWishLimitException ex) {
        log.error("Asmeninių palinkėjimų limitas: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(new ErrorResponse("FAVORITE_WISH_LIMIT", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Neteisingi duomenys: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    // Pagauna visas kitas nenumatytas klaidas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Nenumatyta klaida: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(new ErrorResponse("INTERNAL_ERROR", "Įvyko serverio klaida"));
    }
}
