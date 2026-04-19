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

        // Negalima siųsti sau pačiam
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Negalima siųsti draugystės užklausos sau pačiam");
        }

        // Tikrinama ar tokia užklausa jau egzistuoja (abiem kryptimis)
        if (friendshipRepository.existsBySenderAndReceiver(sender, receiver) ||
                friendshipRepository.existsBySenderAndReceiver(receiver, sender)) {
            throw new IllegalArgumentException("Draugystės užklausa jau egzistuoja");
        }

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setRelationshipType(relationshipType);
        friendship.setStatus(FriendshipStatus.PENDING); // pradinė būsena – laukiama
        friendship.setCreatedAt(LocalDateTime.now());

        friendshipRepository.save(friendship);

        // Pranešame gavėjui apie naują draugystės užklausą (el. paštas – gavėjas gali dar nebūti aktyvus)
        emailService.sendFriendRequestEmail(receiver.getEmail(), receiver.getUsername(), sender.getUsername());

        return toResponse(friendship);
    }

    // Priima draugystės užklausą – keičia statusą į ACCEPTED
    @Transactional
    public FriendshipResponse acceptRequest(String receiverEmail, Long friendshipId) {
        Friendship friendship = findAndValidate(friendshipId, receiverEmail);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        // Pranešame siuntėjui kad jo užklausa priimta (in-app – abu jau registruoti)
        notificationService.create(
                friendship.getSender(),
                NotificationType.FRIEND_REQUEST_ACCEPTED,
                friendship.getReceiver().getUsername() + " priėmė jūsų draugystės užklausą!",
                friendship.getId()
        );

        return toResponse(friendship);
    }

    // Atmeta draugystės užklausą – keičia statusą į DECLINED
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

    // Ištrina draugystę – keičia statusą į REMOVED (abu gali inicijuoti)
    @Transactional
    public void removeFriend(String userEmail, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException("Draugystė nerasta"));

        boolean isSender = friendship.getSender().getEmail().equals(userEmail);
        boolean isReceiver = friendship.getReceiver().getEmail().equals(userEmail);

        if (!isSender && !isReceiver) {
            throw new IllegalArgumentException("Neturite teisės šalinti šios draugystės");
        }

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalArgumentException("Galima šalinti tik aktyvią draugystę");
        }

        friendship.setStatus(FriendshipStatus.REMOVED);
        friendshipRepository.save(friendship);
    }

    // Pagalbinis metodas MessageService naudojimui – tikrina ar du vartotojai draugai
    public boolean areFriends(User user1, User user2) {
        return friendshipRepository.findBySenderAndReceiver(user1, user2)
                .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElseGet(() -> friendshipRepository.findBySenderAndReceiver(user2, user1)
                        .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                        .orElse(false));
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
        return new FriendshipResponse(
                f.getId(),
                f.getSender().getId(),
                f.getSender().getUsername(),
                f.getSender().getFirstName(),
                senderMood,
                senderMood != null ? senderMood.getLabel() : null,
                f.getReceiver().getId(),
                f.getReceiver().getUsername(),
                f.getReceiver().getFirstName(),
                receiverMood,
                receiverMood != null ? receiverMood.getLabel() : null,
                f.getRelationshipType(),
                f.getRelationshipType().getLabel(),
                f.getStatus(),
                f.getCreatedAt()
        );
    }
}