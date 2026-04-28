package com.feelsent.controller;

import com.feelsent.dto.UniqueWishResponse;
import com.feelsent.dto.UserProfileResponse;
import com.feelsent.dto.WishResponse;
import com.feelsent.enums.NotificationType;
import com.feelsent.enums.Role;
import com.feelsent.enums.WishTone;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.User;
import com.feelsent.model.Wish;
import com.feelsent.repository.UserRepository;
import com.feelsent.repository.WishRepository;
import com.feelsent.service.NotificationService;
import com.feelsent.service.UniqueWishService;
import com.feelsent.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Tik adminui pasiekiami endpointai – apsaugoti SecurityConfig per ROLE_ADMIN
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final WishRepository wishRepository;
    private final WishService wishService;
    private final NotificationService notificationService;
    private final UniqueWishService uniqueWishService;

    // GET /api/admin/users – visų vartotojų sąrašas
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userRepository.findAll()
                .stream()
                .map(u -> new UserProfileResponse(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getMoodStatus(),
                        u.getMoodStatus() != null ? u.getMoodStatus().getLabel() : null,
                        u.getMoodWant(),
                        u.getMoodWant() != null ? u.getMoodWant().getLabel() : null,
                        u.getPoints(),
                        u.getRole(),
                        u.getLastLoginAt()
                ))
                .toList();
        return ResponseEntity.ok(users);
    }

    // DELETE /api/admin/users/{id} – šalina vartotoją pagal ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Negalima šalinti administratoriaus");
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    // GET /api/admin/wishes – aktyvūs palinkėjimai su paginacija
    @GetMapping("/wishes")
    public ResponseEntity<Page<WishResponse>> getAllWishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(wishService.getActivePaged(page, size));
    }

    // POST /api/admin/wishes – įterpia naują palinkėjimą į DB
    // Body: { "text": "...", "tone": "SUPPORTIVE", "relationshipType": "ALL" }
    @Caching(evict = {
        @CacheEvict(value = "active-wishes", allEntries = true),
        @CacheEvict(value = "wishes-by-tone", allEntries = true)
    })
    @PostMapping("/wishes")
    public ResponseEntity<WishResponse> addWish(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        String toneRaw = body.get("tone");
        String relationshipType = body.get("relationshipType");

        if (text == null || text.isBlank()) throw new IllegalArgumentException("Laukas 'text' yra privalomas");
        if (toneRaw == null || toneRaw.isBlank()) throw new IllegalArgumentException("Laukas 'tone' yra privalomas");
        if (relationshipType == null || relationshipType.isBlank()) throw new IllegalArgumentException("Laukas 'relationshipType' yra privalomas");

        WishTone tone;
        try {
            tone = WishTone.valueOf(toneRaw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nežinomas tono tipas: " + toneRaw);
        }

        Wish wish = new Wish();
        wish.setText(text);
        wish.setTone(tone);
        wish.setRelationshipType(relationshipType.toUpperCase());
        wish.setActive(true);

        Wish saved = wishRepository.save(wish);

        return ResponseEntity.ok(new WishResponse(
                saved.getId(),
                saved.getText(),
                saved.getTone(),
                saved.getTone().getLabel(),
                saved.getRelationshipType(),
                "/static/images/wishes/" + saved.getId() + ".png"
        ));
    }

    // PATCH /api/admin/wishes/{id}/deactivate – išjungia palinkėjimą (neištrinama iš DB)
    @Caching(evict = {
        @CacheEvict(value = "active-wishes", allEntries = true),
        @CacheEvict(value = "wishes-by-tone", allEntries = true)
    })
    @PatchMapping("/wishes/{id}/deactivate")
    public ResponseEntity<Void> deactivateWish(@PathVariable Long id) {
        Wish wish = wishRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Palinkėjimas nerastas"));
        wish.setActive(false);
        wishRepository.save(wish);
        return ResponseEntity.noContent().build();
    }

    // POST /api/admin/notify/all – siunčia pranešimą visiems eiliniams vartotojams
    @PostMapping("/notify/all")
    public ResponseEntity<Void> notifyAll(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Pranešimo tekstas negali būti tuščias");

        userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.USER)
                .forEach(u -> notificationService.create(u, NotificationType.ADMIN_MESSAGE, text, null));

        return ResponseEntity.noContent().build();
    }

    // POST /api/admin/notify/{userId} – siunčia pranešimą konkrečiam vartotojui
    @PostMapping("/notify/{userId}")
    public ResponseEntity<Void> notifyUser(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Pranešimo tekstas negali būti tuščias");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        notificationService.create(user, NotificationType.ADMIN_MESSAGE, text, null);
        return ResponseEntity.noContent().build();
    }

    // GET /api/admin/unique-wishes – unikalūs palinkėjimai su paginacija
    @GetMapping("/unique-wishes")
    public ResponseEntity<Page<UniqueWishResponse>> getAllUniqueWishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(uniqueWishService.getAllPaged(page, size));
    }

    // POST /api/admin/unique-wishes – sukuria unikalų palinkėjimą
    // Body: { "text": "...", "userId": 5, "expiresAt": "2026-06-01T00:00:00" }
    // userId ir expiresAt neprivalomi
    @PostMapping("/unique-wishes")
    public ResponseEntity<UniqueWishResponse> createUniqueWish(@RequestBody Map<String, Object> body) {
        String text = (String) body.get("text");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Laukas 'text' yra privalomas");

        Long userId = body.get("userId") != null ? Long.valueOf(body.get("userId").toString()) : null;
        LocalDateTime expiresAt = body.get("expiresAt") != null
                ? LocalDateTime.parse(body.get("expiresAt").toString())
                : null;

        return ResponseEntity.ok(uniqueWishService.create(text, userId, expiresAt));
    }

    // PUT /api/admin/unique-wishes/{id} – redaguoja unikalaus palinkėjimo tekstą
    @PutMapping("/unique-wishes/{id}")
    public ResponseEntity<UniqueWishResponse> updateUniqueWish(@PathVariable Long id,
                                                                @RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Laukas 'text' yra privalomas");
        return ResponseEntity.ok(uniqueWishService.update(id, text));
    }

    // POST /api/admin/unique-wishes/{id}/assign – priskiria unikalų palinkėjimą vartotojui
    // Body: { "userId": 5, "expiresAt": "2026-06-01T00:00:00" }
    @PostMapping("/unique-wishes/{id}/assign")
    public ResponseEntity<Void> assignUniqueWish(@PathVariable Long id,
                                                  @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        LocalDateTime expiresAt = body.get("expiresAt") != null
                ? LocalDateTime.parse(body.get("expiresAt").toString())
                : null;
        uniqueWishService.assignToUser(id, userId, expiresAt);
        return ResponseEntity.noContent().build();
    }
}