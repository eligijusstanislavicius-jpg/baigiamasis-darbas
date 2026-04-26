package com.feelsent.controller;

import com.feelsent.dto.WishResponse;
import com.feelsent.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    // GET /api/wishes/suggest/{friendId} – automatiškai pasiūlo 3 palinkėjimus
    // Tonas parenkamas pagal draugo moodWant, filtruojami jau siųsti
    // Jei visi išsiųsti – pradeda iš naujo
    @GetMapping("/suggest/{friendId}")
    public ResponseEntity<List<WishResponse>> suggest(
            @PathVariable Long friendId,
            @RequestParam(defaultValue = "3") int count,
            Authentication authentication) {
        return ResponseEntity.ok(wishService.suggestWishes(authentication.getName(), friendId, count));
    }

    // GET /api/wishes/{id} – vienas palinkėjimas pagal ID (GUESS režimui – po atspėjimo)
    @GetMapping("/{id}")
    public ResponseEntity<WishResponse> getWishById(@PathVariable Long id) {
        return ResponseEntity.ok(wishService.getWishById(id));
    }
}