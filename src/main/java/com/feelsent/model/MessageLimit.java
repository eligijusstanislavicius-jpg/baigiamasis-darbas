package com.feelsent.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "message_limits", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"receiver_id", "sender_id"})
})
@Data
public class MessageLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // kas nustatė limitą (gavėjas)

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // kam taikomas limitas (siuntėjas)

    @Column(nullable = false)
    private Integer dailyLimit; // kiek žinučių per dieną leidžiama siųsti
}