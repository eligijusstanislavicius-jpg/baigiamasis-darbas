package com.feelsent.controller;

import com.feelsent.dto.WishResponse;
import com.feelsent.enums.WishTone;
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
    public ResponseEntity<List<WishResponse>> suggest(@PathVariable Long friendId,
                                                      Authentication authentication) {
        return ResponseEntity.ok(wishService.suggestWishes(authentication.getName(), friendId));
    }

    // GET /api/wishes?receiverId=2&tone=FUNNY – palinkėjimai siuntimui konkrečiam draugui
    // Grąžina tik tinkamus (ryšio tipas + tonas + nesikartoja)
    @GetMapping
    public ResponseEntity<List<WishResponse>> getWishesForSending(
            @RequestParam Long receiverId,
            @RequestParam WishTone tone,
            Authentication authentication) {
        return ResponseEntity.ok(wishService.getWishesForSending(authentication.getName(), receiverId, tone));
    }

    // GET /api/wishes/{id} – vienas palinkėjimas pagal ID (GUESS režimui – po atspėjimo)
    @GetMapping("/{id}")
    public ResponseEntity<WishResponse> getWishById(@PathVariable Long id) {
        return ResponseEntity.ok(wishService.getWishById(id));
    }
}