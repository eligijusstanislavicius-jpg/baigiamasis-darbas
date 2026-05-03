package com.feelsent.service;

import com.feelsent.config.JwtConfig;
import com.feelsent.dto.AuthResponse;
import com.feelsent.dto.LoginRequest;
import com.feelsent.dto.RegisterRequest;
import com.feelsent.dto.UserProfileResponse;
import com.feelsent.enums.MoodStatus;
import com.feelsent.enums.MoodWant;
import com.feelsent.enums.Role;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.User;
import com.feelsent.repository.FavoriteWishRepository;
import com.feelsent.repository.FriendshipRepository;
import com.feelsent.repository.MessageLimitRepository;
import com.feelsent.repository.MessageRepository;
import com.feelsent.repository.NotificationRepository;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final FavoriteWishRepository favoriteWishRepository;
    private final MessageLimitRepository messageLimitRepository;
    private final MessageRepository messageRepository;
    private final FriendshipRepository friendshipRepository;
    private final com.feelsent.repository.UserUniqueWishRepository userUniqueWishRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // Registruoja naują vartotoją ir išsiunčia patvirtinimo laišką
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El. paštas jau užregistruotas");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setPoints(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setEmailVerified(true);
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtConfig.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }

    // Patvirtina el. paštą pagal token'ą ir grąžina JWT
    @Transactional
    public AuthResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Neteisingas arba pasenęs patvirtinimo kodas"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        return new AuthResponse(jwtConfig.generateToken(user.getEmail()), user.getId(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }

    // Prisijungimas – tikrina slaptažodį ir grąžina JWT token'ą
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // authenticationManager pats patikrina email + slaptažodį per CustomUserDetailsService
        // jei neteisingi – automatiškai išmetama klaida
        String email = request.getEmail().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        if (!user.isEmailVerified()) {
            throw new IllegalStateException("El. paštas nepatvirtintas. Patikrinkite savo paštą.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtConfig.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }

    // Grąžina vartotojo profilį pagal el. paštą (iš JWT token'o)
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        return new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMoodStatus(),
                user.getMoodStatus() != null ? user.getMoodStatus().getLabel() : null,
                user.getMoodWant(),
                user.getMoodWant() != null ? user.getMoodWant().getLabel() : null,
                user.getPoints(),
                user.getRole(),
                user.getLastLoginAt()
        );
    }

    // Atnaujina kaip vartotojas jaučiasi
    @Transactional
    public void updateMoodStatus(String email, MoodStatus moodStatus) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
        user.setMoodStatus(moodStatus);
        userRepository.save(user);
    }

    // Atnaujina ko vartotojas norėtų gauti
    @Transactional
    public void updateMoodWant(String email, MoodWant moodWant) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
        user.setMoodWant(moodWant);
        userRepository.save(user);
    }

    // Grąžina taškų progresą iki kito lygio (kiekvienas lygis = 100 taškų)
    public java.util.Map<String, Object> getPointsProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        int points = user.getPoints();
        int remainder = points % 100;
        int pointsToNextLevel = remainder == 0 ? (points == 0 ? 100 : 0) : 100 - remainder;
        int percent = remainder == 0 ? (points == 0 ? 0 : 100) : remainder;

        String rank;
        if (points >= 3000)      rank = "Širdies žmogus";
        else if (points >= 1500) rank = "Mylintysis";
        else if (points >= 800)  rank = "Šiltas žmogus";
        else if (points >= 400)  rank = "Rūpestingasis";
        else if (points >= 150)  rank = "Draugas";
        else                     rank = "Naujokas";

        return java.util.Map.of(
                "points", points,
                "pointsToNextLevel", pointsToNextLevel,
                "percent", percent,
                "rank", rank
        );
    }

    // Pagalbinis metodas – grąžina User objektą pagal el. paštą (naudojamas kituose servisuose)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
    }

    @Transactional
    public void deleteAccount(String email) {
        User user = getByEmail(email);
        if (user.getRole() == com.feelsent.enums.Role.ADMIN) {
            throw new IllegalArgumentException("Administratoriaus paskyros ištrinti negalima");
        }
        notificationRepository.deleteAllByUser(user);
        favoriteWishRepository.deleteAllByUser(user);
        messageLimitRepository.deleteAllByUser(user);
        messageRepository.deleteAllByUser(user);
        friendshipRepository.deleteAllByUser(user);
        userUniqueWishRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }
}
