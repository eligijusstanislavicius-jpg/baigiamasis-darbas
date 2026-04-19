package com.feelsent.config;

import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security reikalauja UserDetailsService – kaip rasti vartotoją pagal vardą
// Mes naudojame email kaip "username" prisijungimui
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())         // BCrypt slaptažodis
                        .roles(user.getRole().name())             // USER arba ADMIN iš DB
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Vartotojas nerastas: " + email));
    }
}
