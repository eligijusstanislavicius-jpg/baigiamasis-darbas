package com.feelsent.controller;

import com.feelsent.dto.AuthResponse;
import com.feelsent.dto.LoginRequest;
import com.feelsent.dto.RegisterRequest;
import com.feelsent.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @Valid – įjungia validaciją (tikrina @NotBlank, @Email ir kt. iš DTO klasių)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // POST /api/auth/register – naujo vartotojo registracija
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    // POST /api/auth/login – prisijungimas
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
