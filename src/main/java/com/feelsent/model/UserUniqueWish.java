package com.feelsent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_unique_wishes")
@Data
public class UserUniqueWish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "unique_wish_id", nullable = false)
    private UniqueWish uniqueWish;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime expiresAt; // null = galioja kol vartotojas ištrina pats

    private LocalDateTime assignedAt;
}
