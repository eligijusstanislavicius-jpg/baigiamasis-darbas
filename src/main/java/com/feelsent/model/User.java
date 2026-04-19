package com.feelsent.model;

import com.feelsent.enums.MoodStatus;
import com.feelsent.enums.MoodWant;
import com.feelsent.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment ID
    private Long id;

    @Column(unique = true, nullable = false) // negali būti du vartotojai su tuo pačiu username
    private String username;

    @Column(nullable = false)
    private String firstName; // rodomas draugams šalia nuotaikos

    @Column(nullable = false)
    private String lastName;  // rodomas profilio peržiūroje

    @Column(unique = true, nullable = false) // naudojamas prisijungimui
    private String email;

    @Column(nullable = false)
    private String passwordHash; // BCrypt užkoduotas slaptažodis

    @Enumerated(EnumType.STRING) // saugoma kaip tekstas DB, pvz. "LAIMINGAS"
    private MoodStatus moodStatus; // kaip vartotojas jaučiasi

    @Enumerated(EnumType.STRING)
    private MoodWant moodWant; // ko vartotojas norėtų gauti

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // numatytasis vaidmuo – paprastas vartotojas

    @Column(nullable = false)
    private Integer points = 0; // bendras taškų kiekis, pradžia 0

    private LocalDateTime createdAt;    // kada sukurtas vartotojas
    private LocalDateTime lastLoginAt;  // paskutinis prisijungimas – naudojama re-engagement logikoje
}