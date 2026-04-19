package com.feelsent.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// @Component – Spring automatiškai sukuria šį objektą ir leidžia jį inject'inti kitur
@Component
public class JwtConfig {

    // Reikšmės paimamos iš application.yaml
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration; // milisekundėmis (86400000 = 24 val.)

    // Sukuria slaptą raktą iš teksto eilutės
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Sugeneruoja JWT token pagal vartotojo vardą
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)                          // į token'ą įrašomas username
                .issuedAt(new Date())                       // kada sukurtas
                .expiration(new Date(System.currentTimeMillis() + expiration)) // kada baigiasi
                .signWith(getSigningKey())                  // pasirašomas slaptuo raktu
                .compact();
    }

    // Ištraukia vartotojo vardą iš token'o
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Patikrina ar token'as galioja (nepasibaigsė, teisingai pasirašytas)
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date()); // tikrina ar dar negaliojo laikas
        } catch (Exception e) {
            return false; // jei bet kokia klaida – token'as negalioja
        }
    }

    // Iššifruoja token'ą ir grąžina visus duomenis (claims)
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // tikrina parašą
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
