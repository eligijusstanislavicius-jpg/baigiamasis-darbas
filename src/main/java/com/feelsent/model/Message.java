package com.feelsent.model;

import com.feelsent.enums.MessageStatus;
import com.feelsent.enums.Reaction;
import com.feelsent.enums.SendMode;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_sender_receiver", columnList = "sender_id, receiver_id"),
    @Index(name = "idx_message_receiver_sent_at", columnList = "receiver_id, sent_at")
})
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // kas siuntė žinutę

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // kas gavo žinutę

    @ManyToOne
    @JoinColumn(name = "wish_id", nullable = false)
    private Wish wish; // palinkėjimas iš DB (visada iš DB – laisvo teksto nėra)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SendMode sendMode; // SIMPLE / GUESS / PASSIVE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status; // SENT → OPENED → GUESSED → REACTED

    private Boolean guessResult; // nullable – true jei gavėjas teisingai atspėjo toną

    @Enumerated(EnumType.STRING)
    private Reaction reaction; // nullable – gavėjo reakcija į palinkėjimą

    private LocalDateTime sentAt; // kada išsiųsta
}
