package com.feelsent.controller;

import com.feelsent.dto.UserProfileResponse;
import com.feelsent.dto.WishResponse;
import com.feelsent.enums.Role;
import com.feelsent.enums.WishTone;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.User;
import com.feelsent.model.Wish;
import com.feelsent.repository.UserRepository;
import com.feelsent.repository.WishRepository;
import com.feelsent.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // GET /api/admin/users – visų vartotojų sąrašas
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userRepository.findAll()
                .stream()
                .map(u -> new UserProfileResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getMoodStatus(),
                        u.getMoodStatus() != null ? u.getMoodStatus().getLabel() : null,
                        u.getMoodWant(),
                        u.getMoodWant() != null ? u.getMoodWant().getLabel() : null,
                        u.getPoints()
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

    // GET /api/admin/wishes – visi aktyvūs palinkėjimai
    @GetMapping("/wishes")
    public ResponseEntity<List<WishResponse>> getAllWishes() {
        return ResponseEntity.ok(wishService.getAllActive());
    }

    // POST /api/admin/wishes – įterpia naują palinkėjimą į DB
    // Body: { "text": "...", "tone": "SUPPORTIVE", "relationshipType": "ALL" }
    @Caching(evict = {
        @CacheEvict(value = "active-wishes", allEntries = true),
        @CacheEvict(value = "wishes-by-tone", allEntries = true)
    })
    @PostMapping("/wishes")
    public ResponseEntity<WishResponse> addWish(@RequestBody Map<String, String> body) {
        Wish wish = new Wish();
        wish.setText(body.get("text"));
        wish.setTone(WishTone.valueOf(body.get("tone")));
        wish.setRelationshipType(body.get("relationshipType"));
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
        return ResponseEntity.ok().build();
    }
}