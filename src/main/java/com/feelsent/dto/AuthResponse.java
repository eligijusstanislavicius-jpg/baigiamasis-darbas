package com.feelsent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Atsakymas kurį klientas gauna po sėkmingo prisijungimo arba registracijos
@Data
@AllArgsConstructor // Lombok: sukuria konstruktorių su visais laukais
public class AuthResponse {

    private String token;
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
}
