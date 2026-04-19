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
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;   // BCrypt slaptažodžių šifravimui
    private final JwtConfig jwtConfig;               // JWT token'ų generavimui
    private final AuthenticationManager authenticationManager; // prisijungimo tikrinimui
    private final EmailService emailService;         // el. laiškų siuntimui

    // Registruoja naują vartotoją ir grąžina JWT token'ą
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El. paštas jau užregistruotas");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // šifruojame slaptažodį
        user.setRole(Role.USER);
        user.setPoints(0);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Siunčiame pasveikinimo laišką fone (neblokuoja registracijos)
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());

        // Generuojame token'ą iš karto po registracijos – vartotojui nereikia atskirai prisijungti
        String token = jwtConfig.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername());
    }

    // Prisijungimas – tikrina slaptažodį ir grąžina JWT token'ą
    public AuthResponse login(LoginRequest request) {
        // authenticationManager pats patikrina email + slaptažodį per CustomUserDetailsService
        // jei neteisingi – automatiškai išmetama klaida
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtConfig.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername());
    }

    // Grąžina vartotojo profilį pagal el. paštą (iš JWT token'o)
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getMoodStatus(),
                user.getMoodStatus() != null ? user.getMoodStatus().getLabel() : null,
                user.getMoodWant(),
                user.getMoodWant() != null ? user.getMoodWant().getLabel() : null,
                user.getPoints()
        );
    }

    // Atnaujina kaip vartotojas jaučiasi
    public void updateMoodStatus(String email, MoodStatus moodStatus) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
        user.setMoodStatus(moodStatus);
        userRepository.save(user);
    }

    // Atnaujina ko vartotojas norėtų gauti
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
        int pointsToNextLevel = 100 - (points % 100); // kiek liko iki kito lygio
        int percent = points % 100;                   // procentas iki kito lygio

        return java.util.Map.of(
                "points", points,
                "pointsToNextLevel", pointsToNextLevel,
                "percent", percent
        );
    }

    // Pagalbinis metodas – grąžina User objektą pagal el. paštą (naudojamas kituose servisuose)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
    }
}
