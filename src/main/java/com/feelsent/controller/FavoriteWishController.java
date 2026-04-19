package com.feelsent.controller;

import com.feelsent.dto.FavoriteWishResponse;
import com.feelsent.dto.FavoriteWishesResponse;
import com.feelsent.service.FavoriteWishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorite-wishes")
@RequiredArgsConstructor
public class FavoriteWishController {

    private final FavoriteWishService favoriteWishService;

    // GET /api/favorite-wishes – visi vartotojo mėgstami palinkėjimai su limito info
    @GetMapping
    public ResponseEntity<FavoriteWishesResponse> getAll(Authentication authentication) {
        return ResponseEntity.ok(favoriteWishService.getAll(authentication.getName()));
    }

    // POST /api/favorite-wishes – išsaugo DB palinkėjimą į mėgstamus
    // Body: { "wishId": 5 }
    @PostMapping
    public ResponseEntity<FavoriteWishResponse> add(@RequestBody Map<String, Long> body,
                                                    Authentication authentication) {
        Long wishId = body.get("wishId");
        if (wishId == null) {
            throw new IllegalArgumentException("Laukas 'wishId' yra privalomas");
        }
        return ResponseEntity.ok(favoriteWishService.add(authentication.getName(), wishId));
    }

    // DELETE /api/favorite-wishes/{id} – ištrina iš mėgstamų
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        favoriteWishService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
