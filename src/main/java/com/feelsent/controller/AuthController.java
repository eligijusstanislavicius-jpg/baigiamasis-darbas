package com.feelsent.controller;

import com.feelsent.dto.AuthResponse;
import com.feelsent.dto.LoginRequest;
import com.feelsent.dto.RegisterRequest;
import com.feelsent.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // POST /api/auth/register – registracija, grąžina JWT iš karto
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    // POST /api/auth/login – prisijungimas
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // GET /api/auth/verify?token=xxx – el. pašto patvirtinimas
    @GetMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@RequestParam String token) {
        return ResponseEntity.ok(userService.verifyEmail(token));
    }
}
