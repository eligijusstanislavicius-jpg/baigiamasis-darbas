package com.feelsent.controller;

import com.feelsent.dto.UserUniqueWishResponse;
import com.feelsent.service.UniqueWishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unique-wishes")
@RequiredArgsConstructor
public class UniqueWishController {

    private final UniqueWishService uniqueWishService;

    // GET /api/unique-wishes/mine – vartotojo unikalūs palinkėjimai
    @GetMapping("/mine")
    public ResponseEntity<List<UserUniqueWishResponse>> getMyUniqueWishes(Authentication auth) {
        return ResponseEntity.ok(uniqueWishService.getMyUniqueWishes(auth.getName()));
    }

    // DELETE /api/unique-wishes/mine/{id} – pašalina iš vartotojo sąrašo
    @DeleteMapping("/mine/{id}")
    public ResponseEntity<Void> removeFromMyList(@PathVariable Long id, Authentication auth) {
        uniqueWishService.removeFromMyList(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
