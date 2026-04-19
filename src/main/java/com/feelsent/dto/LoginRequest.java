package com.feelsent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Duomenys kuriuos siunčia klientas prisijungdamas
@Data
public class LoginRequest {

    @Email(message = "Neteisingas el. pašto formatas")
    @NotBlank(message = "El. paštas negali būti tuščias")
    private String email;

    @NotBlank(message = "Slaptažodis negali būti tuščias")
    private String password;
}
