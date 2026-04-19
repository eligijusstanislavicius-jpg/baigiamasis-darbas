package com.feelsent.controller;

import com.feelsent.dto.UserProfileResponse;
import com.feelsent.enums.MoodStatus;
import com.feelsent.enums.MoodWant;
import com.feelsent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users/me – grąžina prisijungusio vartotojo profilį
    // Authentication – Spring automatiškai įdeda prisijungusio vartotojo duomenis
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName())); // getName() = email
    }

    // PATCH /api/users/me/mood – atnaujina kaip vartotojas jaučiasi
    // @RequestBody Map – priimame JSON: { "moodStatus": "LAIMINGAS" }
    @PatchMapping("/me/mood")
    public ResponseEntity<Void> updateMood(@RequestBody Map<String, String> body,
                                           Authentication authentication) {
        MoodStatus moodStatus = MoodStatus.valueOf(body.get("moodStatus")); // tekstą paverčiame enum
        userService.updateMoodStatus(authentication.getName(), moodStatus);
        return ResponseEntity.ok().build();
    }

    // PATCH /api/users/me/want – atnaujina ko vartotojas norėtų gauti
    @PatchMapping("/me/want")
    public ResponseEntity<Void> updateWant(@RequestBody Map<String, String> body,
                                           Authentication authentication) {
        MoodWant moodWant = MoodWant.valueOf(body.get("moodWant")); // tekstą paverčiame enum
        userService.updateMoodWant(authentication.getName(), moodWant);
        return ResponseEntity.ok().build();
    }

    // GET /api/users/me/points – grąžina taškų progresą
    @GetMapping("/me/points")
    public ResponseEntity<Map<String, Object>> getPoints(Authentication authentication) {
        return ResponseEntity.ok(userService.getPointsProgress(authentication.getName()));
    }
}
