package com.feelsent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_wishes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "wish_id"})
})
@Data
public class FavoriteWish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // kuriam vartotojui priklauso šis mėgstamas palinkėjimas

    @ManyToOne
    @JoinColumn(name = "wish_id", nullable = false)
    private Wish wish; // nuoroda į DB palinkėjimą (ne laisvas tekstas)

    private LocalDateTime createdAt;

    // SVARBU: maksimalus kiekis = 10 vienam vartotojui
    // Ši taisyklė tikrinama FavoriteWishService, ne čia
}
