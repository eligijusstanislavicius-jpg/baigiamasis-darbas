package com.feelsent.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration – Spring žino kad čia yra konfigūracijos klasė
// @EnableWebSecurity – įjungiame Spring Security
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Išjungiame CSRF – REST API nereikia (naudojame JWT)
            .csrf(csrf -> csrf.disable())

            // Nustatome užklausų leidimus
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()                        // registracija ir prisijungimas – laisvi
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()   // Swagger UI
                .requestMatchers("/api/admin/**").hasRole("ADMIN")                  // tik adminui
                .anyRequest().authenticated()                                       // visos kitos užklausos – reikia JWT
            )

            // Išjungiame sesijas – JWT yra stateless (kiekviena užklausa turi token'ą)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Pridedame mūsų JWT filtrą prieš standartinį Spring Security filtrą
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt – slaptažodžių šifravimo algoritmas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager – naudojamas prisijungimo metu slaptažodžio tikrinimui
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
