package com.feelsent.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter – filtras vykdomas vieną kartą kiekvienai užklausai
// Patikrina ar užklausoje yra galiojantis JWT token'as
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skaitome Authorization antraštę iš užklausos
        String authHeader = request.getHeader("Authorization");

        // Jei nėra antraštės arba neprasideda "Bearer " – leidžiame toliau be autentifikacijos
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Ištraukiame token'ą (praleidžiame "Bearer " – 7 simboliai)
        String token = authHeader.substring(7);

        // Tikriname ar token'as galioja
        if (!jwtConfig.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Ištraukiame vartotojo el. paštą iš token'o
        String email = jwtConfig.extractUsername(token);

        // Jei vartotojas dar neautentifikuotas šioje užklausoje
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Sukuriame autentifikacijos objektą ir įdedame į Security kontekstą
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Perduodame užklausą toliau
        filterChain.doFilter(request, response);
    }
}
