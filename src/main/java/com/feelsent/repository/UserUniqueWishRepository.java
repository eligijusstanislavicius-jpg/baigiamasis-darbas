package com.feelsent.repository;

import com.feelsent.model.UniqueWish;
import com.feelsent.model.User;
import com.feelsent.model.UserUniqueWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserUniqueWishRepository extends JpaRepository<UserUniqueWish, Long> {

    List<UserUniqueWish> findAllByUser(User user);

    List<UserUniqueWish> findAllByUniqueWish(UniqueWish uniqueWish);

    boolean existsByUserAndUniqueWish(User user, UniqueWish uniqueWish);

    void deleteAllByUser(User user);

    @Modifying
    @Query("DELETE FROM UserUniqueWish u WHERE u.expiresAt IS NOT NULL AND u.expiresAt < :now")
    void deleteAllExpired(LocalDateTime now);
}
