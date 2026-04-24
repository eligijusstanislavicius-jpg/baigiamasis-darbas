package com.feelsent.service;

import com.feelsent.dto.WishResponse;
import com.feelsent.enums.FriendshipStatus;
import com.feelsent.enums.MoodWant;
import com.feelsent.enums.WishTone;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.Friendship;
import com.feelsent.model.Message;
import com.feelsent.model.User;
import com.feelsent.model.Wish;
import com.feelsent.repository.FavoriteWishRepository;
import com.feelsent.repository.FriendshipRepository;
import com.feelsent.repository.MessageRepository;
import com.feelsent.repository.UserRepository;
import com.feelsent.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishService {

    private static final int SUGGEST_COUNT = 3; // kiek pasiūlymų grąžiname siuntėjui

    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;
    private final FavoriteWishRepository favoriteWishRepository;

    // Automatiškai pasiūlo 3 palinkėjimus pagal draugo moodWant + ryšio tipą
    // Filtravimo lygiai:
    //   1. Nesiūlyti jau išsiųstų ŠI draugui + jau išsaugotų siuntėjo mėgstamuose
    //   2. Jei nelieka 3 – leisti mėgstamas (bet ne siųstas)
    //   3. Jei visi šio tono siųsti – imti iš viso aktyvaus sąrašo (reset)
    public List<WishResponse> suggestWishes(String senderEmail, Long friendId) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Siuntėjas nerastas"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("Draugas nerastas"));

        String relationshipType = getRelationshipType(sender, friend);
        WishTone tone = moodWantToTone(friend.getMoodWant());

        List<Wish> candidates = wishRepository.findByToneAndRelationshipTypeOrAll(tone, relationshipType);

        Set<Long> sentIds = getAlreadySentWishIds(sender, friend);
        Set<Long> favoriteIds = favoriteWishRepository.findAllByUser(sender)
                .stream()
                .map(fw -> fw.getWish().getId())
                .collect(Collectors.toSet());

        // 1 lygis: tinkamas tonas + ryšys, be siųstų ir be mėgstamų
        List<Wish> available = candidates.stream()
                .filter(w -> !sentIds.contains(w.getId()) && !favoriteIds.contains(w.getId()))
                .collect(Collectors.toList());

        // 2 lygis: visi aktyvūs šio ryšio tipo, be siųstų ir be mėgstamų (kiti tonai)
        if (available.size() < SUGGEST_COUNT) {
            List<Wish> broader = wishRepository.findByRelationshipTypeAndActiveTrue(relationshipType);
            available = broader.stream()
                    .filter(w -> !sentIds.contains(w.getId()) && !favoriteIds.contains(w.getId()))
                    .collect(Collectors.toList());
        }

        // 3 lygis: visi aktyvūs, be siųstų ir be mėgstamų (bet koks ryšys)
        if (available.size() < SUGGEST_COUNT) {
            available = wishRepository.findAllByActiveTrue().stream()
                    .filter(w -> !sentIds.contains(w.getId()) && !favoriteIds.contains(w.getId()))
                    .collect(Collectors.toList());
        }

        // 4 lygis: reset – ignoruoti siuntimo istoriją, bet vis dar ne mėgstamas
        if (available.size() < SUGGEST_COUNT) {
            available = wishRepository.findAllByActiveTrue().stream()
                    .filter(w -> !favoriteIds.contains(w.getId()))
                    .collect(Collectors.toList());
        }

        // 5 lygis: viskas išsaugota mėgstamuose – leisti bet ką
        if (available.isEmpty()) {
            available = new java.util.ArrayList<>(wishRepository.findAllByActiveTrue());
        }

        Collections.shuffle(available);
        return available.stream()
                .limit(SUGGEST_COUNT)
                .map(this::toResponse)
                .toList();
    }

    // Grąžina tinkamus palinkėjimus siuntimui konkrečiam gavėjui pagal pasirinktą toną
    // Filtruoja: ryšio tipas + tonas + nepakartoja jau siųstų
    public List<WishResponse> getWishesForSending(String senderEmail, Long receiverId, WishTone tone) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Siuntėjas nerastas"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Gavėjas nerastas"));

        String relationshipType = getRelationshipType(sender, receiver);

        List<Wish> candidates = wishRepository.findByToneAndRelationshipTypeOrAll(tone, relationshipType);

        Set<Long> alreadySentIds = getAlreadySentWishIds(sender, receiver);

        List<Wish> available = candidates.stream()
                .filter(w -> !alreadySentIds.contains(w.getId()))
                .collect(Collectors.toList());

        // Jei visi išsiųsti – pradedame iš naujo
        if (available.isEmpty()) {
            available = new java.util.ArrayList<>(candidates);
        }

        return available.stream()
                .map(this::toResponse)
                .toList();
    }

    // Grąžina vieną aktyvų palinkėjimą pagal ID (naudojama atidarius "voką" GUESS režime)
    public WishResponse getWishById(Long id) {
        Wish wish = wishRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Palinkėjimas nerastas"));
        return toResponse(wish);
    }

    // Grąžina visus aktyvius palinkėjimus (administravimui / peržiūrai)
    public List<WishResponse> getAllActive() {
        return wishRepository.findAllByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Draugo moodWant → WishTone (automatinis pasiūlymų tonas)
    private WishTone moodWantToTone(MoodWant moodWant) {
        if (moodWant == null) return WishTone.SUPPORTIVE;
        return switch (moodWant) {
            case CHEER_ME_UP, SURPRISE_ME -> WishTone.FUNNY;
            default -> WishTone.SUPPORTIVE;
        };
    }

    // Randa ryšio tipą tarp siuntėjo ir gavėjo pagal draugystės įrašą
    private String getRelationshipType(User sender, User receiver) {
        return friendshipRepository.findBySenderAndReceiver(sender, receiver)
                .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .map(f -> f.getRelationshipType().name())
                .orElseGet(() -> friendshipRepository.findBySenderAndReceiver(receiver, sender)
                        .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                        .map(f -> f.getRelationshipType().name())
                        .orElseThrow(() -> new IllegalArgumentException("Vartotojai nėra draugai")));
    }

    // Surinka visų jau siųstų palinkėjimų ID tarp šios poros (abiem kryptimis)
    private Set<Long> getAlreadySentWishIds(User sender, User receiver) {
        Set<Long> ids = messageRepository.findAllBySenderAndReceiver(sender, receiver)
                .stream()
                .filter(m -> m.getWish() != null)
                .map(m -> m.getWish().getId())
                .collect(Collectors.toSet());

        messageRepository.findAllBySenderAndReceiver(receiver, sender)
                .stream()
                .filter(m -> m.getWish() != null)
                .map(m -> m.getWish().getId())
                .forEach(ids::add);

        return ids;
    }

    // Paverčia Wish entity į DTO, prideda paveikslėlio URL
    private WishResponse toResponse(Wish w) {
        return new WishResponse(
                w.getId(),
                w.getText(),
                w.getTone(),
                w.getTone().getLabel(),
                w.getRelationshipType(),
                "/static/images/wishes/" + w.getId() + ".png"
        );
    }
}
