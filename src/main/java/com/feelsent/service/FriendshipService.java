package com.feelsent.service;

import com.feelsent.dto.FriendshipResponse;
import com.feelsent.enums.FriendshipStatus;
import com.feelsent.enums.MoodStatus;
import com.feelsent.enums.NotificationType;
import com.feelsent.enums.RelationshipType;
import com.feelsent.exception.FriendshipNotFoundException;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.Friendship;
import com.feelsent.model.User;
import com.feelsent.repository.FriendshipRepository;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    // Siunčia draugystės užklausą kitam vartotojui
    @Transactional
    public FriendshipResponse sendRequest(String senderEmail, Long receiverId, RelationshipType relationshipType) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Siuntėjas nerastas"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Gavėjas nerastas"));

        if (relationshipType == null) {
            throw new IllegalArgumentException("Ryšio tipas yra privalomas (relationshipType)");
        }

        // Negalima siųsti sau pačiam
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Negalima siųsti draugystės užklausos sau pačiam");
        }

        // Tikrinama ar egzistuoja aktyvi draugystė (PENDING arba ACCEPTED) – abiem kryptimis
        if (friendshipRepository.existsActiveRelationship(sender, receiver) ||
                friendshipRepository.existsActiveRelationship(receiver, sender)) {
            throw new IllegalArgumentException("Draugystės užklausa jau egzistuoja arba esate draugai");
        }

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setSenderRelationshipType(relationshipType);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());

        friendshipRepository.save(friendship);

        // Pranešame gavėjui apie naują draugystės užklausą (el. paštas – gavėjas gali dar nebūti aktyvus)
        emailService.sendFriendRequestEmail(receiver.getEmail(), receiver.getFirstName(), sender.getFirstName());

        return toResponse(friendship);
    }

    // Priima draugystės užklausą – keičia statusą į ACCEPTED ir nustato gavėjo ryšio tipą
    @Transactional
    public FriendshipResponse acceptRequest(String receiverEmail, Long friendshipId, RelationshipType receiverRelationshipType) {
        if (receiverRelationshipType == null) {
            throw new IllegalArgumentException("Pasirinkite kas jums yra šis žmogus");
        }
        Friendship friendship = findAndValidate(friendshipId, receiverEmail);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setReceiverRelationshipType(receiverRelationshipType);
        friendshipRepository.save(friendship);

        // Pranešame siuntėjui kad jo užklausa priimta (in-app – abu jau registruoti)
        notificationService.create(
                friendship.getSender(),
                NotificationType.FRIEND_REQUEST_ACCEPTED,
                friendship.getReceiver().getFirstName() + " priėmė jūsų draugystės užklausą!",
                friendship.getId()
        );

        return toResponse(friendship);
    }

    // Atmeta draugystės užklausą – keičia statusą į DECLINED
    @Transactional
    public FriendshipResponse declineRequest(String receiverEmail, Long friendshipId) {
        Friendship friendship = findAndValidate(friendshipId, receiverEmail);
        friendship.setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);
        return toResponse(friendship);
    }

    // Grąžina visas gautas PENDING užklausas (dar neatsaksyta)
    public List<FriendshipResponse> getPendingRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        return friendshipRepository.findAllByReceiverAndStatus(user, FriendshipStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Grąžina visų draugų sąrašą (ACCEPTED iš abiejų pusių)
    public List<FriendshipResponse> getFriends(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));

        // JOIN FETCH – sender ir receiver kraunami viena užklausa, ne N+1 lazy-load
        List<Friendship> asSender = friendshipRepository.findAllBySenderAndStatusWithUsers(user, FriendshipStatus.ACCEPTED);
        List<Friendship> asReceiver = friendshipRepository.findAllByReceiverAndStatusWithUsers(user, FriendshipStatus.ACCEPTED);

        List<FriendshipResponse> result = new ArrayList<>();
        asSender.stream().map(this::toResponse).forEach(result::add);
        asReceiver.stream().map(this::toResponse).forEach(result::add);

        return result;
    }

    // Ištrina draugystę – keičia statusą į REMOVED arba atšaukia PENDING užklausą
    @Transactional
    public void removeFriend(String userEmail, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException("Draugystė nerasta"));

        boolean isSender = friendship.getSender().getEmail().equals(userEmail);
        boolean isReceiver = friendship.getReceiver().getEmail().equals(userEmail);

        if (!isSender && !isReceiver) {
            throw new IllegalArgumentException("Neturite teisės šalinti šios draugystės");
        }

        if (friendship.getStatus() == FriendshipStatus.PENDING && isSender) {
            friendshipRepository.delete(friendship);
            return;
        }

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalArgumentException("Galima šalinti tik aktyvią draugystę");
        }

        friendship.setStatus(FriendshipStatus.REMOVED);
        friendshipRepository.save(friendship);

        User remover = isSender ? friendship.getSender() : friendship.getReceiver();
        User removed = isSender ? friendship.getReceiver() : friendship.getSender();
        notificationService.create(
                removed,
                NotificationType.FRIEND_REMOVED,
                remover.getFirstName() + " pašalino tave iš draugų sąrašo",
                friendship.getId()
        );
    }

    // Pagalbinis metodas MessageService naudojimui – tikrina ar du vartotojai draugai
    public boolean areFriends(User user1, User user2) {
        return friendshipRepository.findFirstBySenderAndReceiverAndStatus(user1, user2, FriendshipStatus.ACCEPTED).isPresent()
                || friendshipRepository.findFirstBySenderAndReceiverAndStatus(user2, user1, FriendshipStatus.ACCEPTED).isPresent();
    }

    // Randa draugystę ir patikrina ar šis vartotojas yra gavėjas (tik gavėjas gali priimti/atmesti)
    private Friendship findAndValidate(Long friendshipId, String receiverEmail) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException("Draugystės užklausa nerasta"));

        if (!friendship.getReceiver().getEmail().equals(receiverEmail)) {
            throw new IllegalArgumentException("Neturite teisės keisti šios užklausos");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Užklausa jau apdorota");
        }

        return friendship;
    }

    // Paverčia Friendship entity į DTO (grąžinamą klientui)
    private FriendshipResponse toResponse(Friendship f) {
        MoodStatus senderMood = f.getSender().getMoodStatus();
        MoodStatus receiverMood = f.getReceiver().getMoodStatus();
        RelationshipType senderRel = f.getSenderRelationshipType();
        RelationshipType receiverRel = f.getReceiverRelationshipType();
        return new FriendshipResponse(
                f.getId(),
                f.getSender().getId(),
                f.getSender().getFirstName(),
                f.getSender().getLastName(),
                senderMood,
                senderMood != null ? senderMood.getLabel() : null,
                f.getReceiver().getId(),
                f.getReceiver().getFirstName(),
                f.getReceiver().getLastName(),
                receiverMood,
                receiverMood != null ? receiverMood.getLabel() : null,
                senderRel,
                senderRel != null ? senderRel.getLabel() : null,
                receiverRel,
                receiverRel != null ? receiverRel.getLabel() : null,
                f.getStatus(),
                f.getCreatedAt()
        );
    }
}