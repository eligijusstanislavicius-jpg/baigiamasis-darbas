package com.feelsent.model;

import com.feelsent.enums.WishTone;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "wishes")
@Data
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ID = paveikslėlio pavadinimas, pvz. id=1 → /static/images/wishes/1.png
    private Long id;

    @Column(nullable = false, length = 500)
    private String text; // palinkėjimo tekstas

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WishTone tone; // palinkėjimo tonas: SUPPORTIVE, FUNNY, ROMANTIC, BIRTHDAY

    @Column(nullable = false)
    private String relationshipType; // String, nes gali būti "VISI" arba "MAMA", "PARTNERIS" ir t.t.

    @Column(nullable = false)
    private Boolean active = true; // true = aktyvus, false = išjungtas (neištrinama iš DB)
}