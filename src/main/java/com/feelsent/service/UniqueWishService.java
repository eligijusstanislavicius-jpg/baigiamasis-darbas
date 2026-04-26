package com.feelsent.service;

import com.feelsent.dto.UniqueWishResponse;
import com.feelsent.dto.UserUniqueWishResponse;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.UniqueWish;
import com.feelsent.model.User;
import com.feelsent.model.UserUniqueWish;
import com.feelsent.repository.UniqueWishRepository;
import com.feelsent.repository.UserRepository;
import com.feelsent.repository.UserUniqueWishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UniqueWishService {

    private final UniqueWishRepository uniqueWishRepository;
    private final UserUniqueWishRepository userUniqueWishRepository;
    private final UserRepository userRepository;

    // Adminas sukuria unikalų palinkėjimą; pasirinktinai iš karto priskiria vartotojui
    @Transactional
    public UniqueWishResponse create(String text, Long userId, LocalDateTime expiresAt) {
        UniqueWish wish = new UniqueWish();
        wish.setText(text);
        wish.setCreatedAt(LocalDateTime.now());
        uniqueWishRepository.save(wish);

        if (userId != null) {
            assign(wish, userId, expiresAt);
        }

        return toResponse(wish);
    }

    // Adminas redaguoja palinkėjimo tekstą
    @Transactional
    public UniqueWishResponse update(Long id, String text) {
        UniqueWish wish = uniqueWishRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unikalus palinkėjimas nerastas"));
        wish.setText(text);
        uniqueWishRepository.save(wish);
        return toResponse(wish);
    }

    // Adminas priskiria esamą unikalų palinkėjimą vartotojui
    @Transactional
    public void assignToUser(Long uniqueWishId, Long userId, LocalDateTime expiresAt) {
        UniqueWish wish = uniqueWishRepository.findById(uniqueWishId)
                .orElseThrow(() -> new IllegalArgumentException("Unikalus palinkėjimas nerastas"));
        assign(wish, userId, expiresAt);
    }

    // Adminas mato visus unikalius palinkėjimus
    public List<UniqueWishResponse> getAll() {
        return uniqueWishRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // Vartotojas mato savo unikalius palinkėjimus
    public List<UserUniqueWishResponse> getMyUniqueWishes(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
        return userUniqueWishRepository.findAllByUser(user).stream()
                .map(this::toUserResponse)
                .toList();
    }

    // Vartotojas pašalina unikalų palinkėjimą iš savo sąrašo
    @Transactional
    public void removeFromMyList(String email, Long userUniqueWishId) {
        UserUniqueWish assignment = userUniqueWishRepository.findById(userUniqueWishId)
                .orElseThrow(() -> new IllegalArgumentException("Palinkėjimas nerastas"));
        if (!assignment.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Neturite teisės šalinti šio palinkėjimo");
        }
        userUniqueWishRepository.delete(assignment);
    }

    // Scheduler naudoja — šalina pasibaigusio galiojimo priskyrimus
    @Transactional
    public void expireOldAssignments() {
        userUniqueWishRepository.deleteAllExpired(LocalDateTime.now());
    }

    private void assign(UniqueWish wish, Long userId, LocalDateTime expiresAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        if (userUniqueWishRepository.existsByUserAndUniqueWish(user, wish)) {
            throw new IllegalArgumentException("Šis palinkėjimas jau priskirtas šiam vartotojui");
        }

        UserUniqueWish assignment = new UserUniqueWish();
        assignment.setUniqueWish(wish);
        assignment.setUser(user);
        assignment.setExpiresAt(expiresAt);
        assignment.setAssignedAt(LocalDateTime.now());
        userUniqueWishRepository.save(assignment);
    }

    private UniqueWishResponse toResponse(UniqueWish w) {
        List<UniqueWishResponse.AssignmentInfo> assignments =
                userUniqueWishRepository.findAllByUniqueWish(w).stream()
                        .map(a -> new UniqueWishResponse.AssignmentInfo(
                                a.getId(),
                                a.getUser().getId(),
                                a.getUser().getFirstName(),
                                a.getUser().getLastName(),
                                a.getExpiresAt(),
                                a.getAssignedAt()
                        ))
                        .toList();
        return new UniqueWishResponse(w.getId(), w.getText(), w.getCreatedAt(), assignments);
    }

    private UserUniqueWishResponse toUserResponse(UserUniqueWish a) {
        return new UserUniqueWishResponse(
                a.getId(),
                a.getUniqueWish().getId(),
                a.getUniqueWish().getText(),
                a.getExpiresAt(),
                a.getAssignedAt()
        );
    }
}
