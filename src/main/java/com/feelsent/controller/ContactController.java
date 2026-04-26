package com.feelsent.controller;

import com.feelsent.enums.NotificationType;
import com.feelsent.enums.Role;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.User;
import com.feelsent.repository.UserRepository;
import com.feelsent.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // POST /api/contact – vartotojas siunčia žinutę visiems administratoriams
    @PostMapping
    public ResponseEntity<Void> contactAdmin(@RequestBody Map<String, String> body,
                                             Authentication auth) {
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Žinutės tekstas negali būti tuščias");
        }

        User sender = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        String message = "Žinutė nuo " + sender.getFirstName() + " " + sender.getLastName() + ": " + text;

        userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .forEach(admin -> notificationService.create(admin, NotificationType.CONTACT_ADMIN, message, null));

        return ResponseEntity.noContent().build();
    }
}
