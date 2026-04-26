package com.feelsent.service;

import com.feelsent.config.JwtConfig;
import com.feelsent.dto.AuthResponse;
import com.feelsent.dto.RegisterRequest;
import com.feelsent.dto.LoginRequest;
import com.feelsent.enums.Role;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.User;
import com.feelsent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtConfig jwtConfig;
    @Mock AuthenticationManager authenticationManager;
    @Mock EmailService emailService;
    @Mock NotificationRepository notificationRepository;
    @Mock FavoriteWishRepository favoriteWishRepository;
    @Mock MessageLimitRepository messageLimitRepository;
    @Mock MessageRepository messageRepository;
    @Mock FriendshipRepository friendshipRepository;
    @Mock UserUniqueWishRepository userUniqueWishRepository;

    @InjectMocks
    UserService userService;

    private User vartotojas;

    @BeforeEach
    void setUp() {
        vartotojas = new User();
        vartotojas.setId(1L);
        vartotojas.setFirstName("Jonas");
        vartotojas.setLastName("Jonaitis");
        vartotojas.setEmail("jonas@test.lt");
        vartotojas.setPasswordHash("hash");
        vartotojas.setRole(Role.USER);
        vartotojas.setPoints(0);
        vartotojas.setEmailVerified(true);
    }

    // --- register() ---

    @Test
    void register_sukuria_nauja_vartotoja() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Jonas");
        request.setLastName("Jonaitis");
        request.setEmail("jonas@test.lt");
        request.setPassword("slaptazodis");

        when(userRepository.existsByEmail("jonas@test.lt")).thenReturn(false);
        when(passwordEncoder.encode("slaptazodis")).thenReturn("hash");
        when(userRepository.save(any())).thenReturn(vartotojas);
        when(jwtConfig.generateToken("jonas@test.lt")).thenReturn("jwt-token");

        AuthResponse result = userService.register(request);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getRole()).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_meta_klaida_kai_el_pastas_jau_uzimtas() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("jonas@test.lt");

        when(userRepository.existsByEmail("jonas@test.lt")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jau užregistruotas");
    }

    // --- login() ---

    @Test
    void login_sekmingai_grazina_token() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jonas@test.lt");
        request.setPassword("slaptazodis");

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));
        when(jwtConfig.generateToken("jonas@test.lt")).thenReturn("jwt-token");

        AuthResponse result = userService.login(request);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getRole()).isEqualTo("USER");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_meta_klaida_kai_el_pastas_nepatvirtintas() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jonas@test.lt");
        request.setPassword("slaptazodis");

        vartotojas.setEmailVerified(false);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nepatvirtintas");
    }

    // --- getPointsProgress() rangai ---

    @Test
    void getPointsProgress_0_tasku_rangas_naujokas() {
        vartotojas.setPoints(0);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        Map<String, Object> result = userService.getPointsProgress("jonas@test.lt");

        assertThat(result.get("rank")).isEqualTo("Naujokas");
        assertThat(result.get("points")).isEqualTo(0);
        assertThat(result.get("pointsToNextLevel")).isEqualTo(100);
    }

    @Test
    void getPointsProgress_150_tasku_rangas_draugas() {
        vartotojas.setPoints(150);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        Map<String, Object> result = userService.getPointsProgress("jonas@test.lt");

        assertThat(result.get("rank")).isEqualTo("Draugas");
    }

    @Test
    void getPointsProgress_400_tasku_rangas_rupestingasis() {
        vartotojas.setPoints(400);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        Map<String, Object> result = userService.getPointsProgress("jonas@test.lt");

        assertThat(result.get("rank")).isEqualTo("Rūpestingasis");
    }

    @Test
    void getPointsProgress_800_tasku_rangas_siltas_zmogus() {
        vartotojas.setPoints(800);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        Map<String, Object> result = userService.getPointsProgress("jonas@test.lt");

        assertThat(result.get("rank")).isEqualTo("Šiltas žmogus");
    }

    @Test
    void getPointsProgress_1500_tasku_rangas_mylintysis() {
        vartotojas.setPoints(1500);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        Map<String, Object> result = userService.getPointsProgress("jonas@test.lt");

        assertThat(result.get("rank")).isEqualTo("Mylintysis");
    }

    @Test
    void getPointsProgress_3000_tasku_rangas_sirdies_zmogus() {
        vartotojas.setPoints(3000);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        Map<String, Object> result = userService.getPointsProgress("jonas@test.lt");

        assertThat(result.get("rank")).isEqualTo("Širdies žmogus");
    }

    // --- deleteAccount() ---

    @Test
    void deleteAccount_meta_klaida_adminui() {
        vartotojas.setRole(Role.ADMIN);
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        assertThatThrownBy(() -> userService.deleteAccount("jonas@test.lt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Administratoriaus");
    }

    @Test
    void deleteAccount_istrina_vartotoja_su_visais_duomenimis() {
        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(vartotojas));

        userService.deleteAccount("jonas@test.lt");

        verify(notificationRepository).deleteAllByUser(vartotojas);
        verify(favoriteWishRepository).deleteAllByUser(vartotojas);
        verify(messageLimitRepository).deleteAllByUser(vartotojas);
        verify(messageRepository).deleteAllByUser(vartotojas);
        verify(friendshipRepository).deleteAllByUser(vartotojas);
        verify(userUniqueWishRepository).deleteAllByUser(vartotojas);
        verify(userRepository).delete(vartotojas);
    }
}
