package com.feelsent.service;

import com.feelsent.dto.FavoriteWishResponse;
import com.feelsent.dto.FavoriteWishesResponse;
import com.feelsent.exception.FavoriteWishLimitException;
import com.feelsent.exception.UserNotFoundException;
import com.feelsent.model.FavoriteWish;
import com.feelsent.model.User;
import com.feelsent.model.Wish;
import com.feelsent.repository.FavoriteWishRepository;
import com.feelsent.repository.UserRepository;
import com.feelsent.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteWishService {

    @Value("${app.limits.max-favorite-wishes}")
    private int maxFavoriteWishes;

    private final FavoriteWishRepository favoriteWishRepository;
    private final UserRepository userRepository;
    private final WishRepository wishRepository;

    // Grąžina visus vartotojo mėgstamus palinkėjimus su limito informacija
    public FavoriteWishesResponse getAll(String email) {
        User user = getUser(email);
        List<FavoriteWishResponse> wishes = favoriteWishRepository.findAllByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
        long count = wishes.size();
        return new FavoriteWishesResponse(wishes, count, maxFavoriteWishes - count, maxFavoriteWishes);
    }

    // Išsaugo DB palinkėjimą į mėgstamus (tikrina 10 vnt. limitą ir dublikatus)
    @Transactional
    public FavoriteWishResponse add(String email, Long wishId) {
        User user = getUser(email);

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("Palinkėjimas nerastas"));

        if (favoriteWishRepository.existsByUserAndWish(user, wish)) {
            throw new IllegalArgumentException("Šis palinkėjimas jau išsaugotas mėgstamuose");
        }

        long count = favoriteWishRepository.countByUser(user);
        if (count >= maxFavoriteWishes) {
            throw new FavoriteWishLimitException(
                    "Pasiektas maksimalus mėgstamų palinkėjimų skaičius (" + maxFavoriteWishes + ")");
        }

        FavoriteWish favoriteWish = new FavoriteWish();
        favoriteWish.setUser(user);
        favoriteWish.setWish(wish);
        favoriteWish.setCreatedAt(LocalDateTime.now());

        return toResponse(favoriteWishRepository.save(favoriteWish));
    }

    // Ištrina mėgstamą palinkėjimą – tikrina kad priklauso šiam vartotojui
    @Transactional
    public void delete(String email, Long id) {
        User user = getUser(email);
        FavoriteWish favoriteWish = favoriteWishRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Mėgstamas palinkėjimas nerastas"));
        favoriteWishRepository.delete(favoriteWish);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Vartotojas nerastas"));
    }

    private FavoriteWishResponse toResponse(FavoriteWish fw) {
        return new FavoriteWishResponse(
                fw.getId(),
                fw.getWish().getId(),
                fw.getWish().getText(),
                fw.getWish().getTone(),
                fw.getWish().getTone().getLabel(),
                "/static/images/wishes/" + fw.getWish().getId() + ".png",
                fw.getCreatedAt()
        );
    }
}
