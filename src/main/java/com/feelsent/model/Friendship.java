package com.feelsent.model;

import com.feelsent.enums.FriendshipStatus;
import com.feelsent.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships", indexes = {
    @Index(name = "idx_friendship_sender_status", columnList = "sender_id, status"),
    @Index(name = "idx_friendship_receiver_status", columnList = "receiver_id, status")
})
@Data
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // daug draugysčių gali turėti tą patį siuntėją
    @JoinColumn(name = "sender_id", nullable = false) // DB stulpelio pavadinimas
    private User sender; // kas siunčia draugystės užklausą

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // kas gauna draugystės užklausą

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationshipType relationshipType; // ryšio tipas: MAMA, PARTNERIS, DRAUGAS...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status; // PENDING / ACCEPTED / DECLINED / REMOVED

    private LocalDateTime createdAt;
}