package com.feelsent.repository;

import com.feelsent.model.FavoriteWish;
import com.feelsent.model.User;
import com.feelsent.model.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteWishRepository extends JpaRepository<FavoriteWish, Long> {

    List<FavoriteWish> findAllByUser(User user); // visi vartotojo mėgstami palinkėjimai

    long countByUser(User user); // kiek vartotojas turi mėgstamų (max 10)

    Optional<FavoriteWish> findByIdAndUser(Long id, User user); // ieško pagal ID ir vartotoją (saugumui)

    boolean existsByUserAndWish(User user, Wish wish); // tikrina ar šis palinkėjimas jau išsaugotas

    void deleteAllByUser(User user);
}
