package com.feelsent.model;

import com.feelsent.enums.FriendshipStatus;
import com.feelsent.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships",
    indexes = {
        @Index(name = "idx_friendship_sender_status", columnList = "sender_id, status"),
        @Index(name = "idx_friendship_receiver_status", columnList = "receiver_id, status")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
    }
)
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
    @Column(name = "sender_relationship_type", nullable = false)
    private RelationshipType senderRelationshipType; // kaip siuntėjas mato gavėją

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_relationship_type")
    private RelationshipType receiverRelationshipType; // kaip gavėjas mato siuntėją (nustatoma priimant)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status; // PENDING / ACCEPTED / DECLINED / REMOVED

    private LocalDateTime createdAt;
}