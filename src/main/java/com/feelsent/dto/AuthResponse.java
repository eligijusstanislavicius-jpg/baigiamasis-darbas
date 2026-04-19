package com.feelsent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Atsakymas kurį klientas gauna po sėkmingo prisijungimo arba registracijos
@Data
@AllArgsConstructor // Lombok: sukuria konstruktorių su visais laukais
public class AuthResponse {

    private String token;    // JWT token'as (klientas saugo ir siunčia su kiekviena užklausa)
    private String username; // vartotojo vardas (rodymui)
}
