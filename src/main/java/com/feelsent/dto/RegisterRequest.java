package com.feelsent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Duomenys kuriuos siunčia klientas registruodamasis
@Data
public class RegisterRequest {

    @NotBlank(message = "Vardas negali būti tuščias")
    private String firstName;

    @NotBlank(message = "Pavardė negali būti tuščia")
    private String lastName;

    @Email(message = "Neteisingas el. pašto formatas")
    @NotBlank(message = "El. paštas negali būti tuščias")
    private String email;

    @NotBlank(message = "Slaptažodis negali būti tuščias")
    @Size(min = 6, message = "Slaptažodis turi būti bent 6 simboliai")
    private String password;
}
