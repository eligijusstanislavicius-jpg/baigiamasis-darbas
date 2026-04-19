package com.feelsent.model;

import com.feelsent.enums.PointReason;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_transactions")
@Data
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // kam priskirti taškai

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message; // už kurią žinutę gauti taškai

    @Column(nullable = false)
    private Integer points; // kiek taškų (GUESS_CORRECT = 5, REACTION_RECEIVED = 10)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointReason reason; // priežastis: GUESS_CORRECT arba REACTION_RECEIVED

    private LocalDateTime createdAt;
}