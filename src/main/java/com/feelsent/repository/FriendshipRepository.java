package com.feelsent.repository;

import com.feelsent.enums.FriendshipStatus;
import com.feelsent.model.Friendship;
import com.feelsent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findFirstBySenderAndReceiverAndStatus(User sender, User receiver, FriendshipStatus status);

    // JOIN FETCH užkrauna sender ir receiver viena užklausa – pašalina N+1 problemą
    @Query("SELECT f FROM Friendship f JOIN FETCH f.sender JOIN FETCH f.receiver WHERE f.sender = :user AND f.status = :status")
    List<Friendship> findAllBySenderAndStatusWithUsers(@Param("user") User user, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.sender JOIN FETCH f.receiver WHERE f.receiver = :user AND f.status = :status")
    List<Friendship> findAllByReceiverAndStatusWithUsers(@Param("user") User user, @Param("status") FriendshipStatus status);

    List<Friendship> findAllByReceiverAndStatus(User receiver, FriendshipStatus status);

    // Tikrina ar egzistuoja aktyvi draugystė (PENDING arba ACCEPTED) – DECLINED/REMOVED leidžia pakartotinę užklausą
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE f.sender = :sender AND f.receiver = :receiver AND f.status IN ('PENDING', 'ACCEPTED')")
    boolean existsActiveRelationship(@Param("sender") User sender, @Param("receiver") User receiver);

    @Modifying
    @Query("DELETE FROM Friendship f WHERE f.sender = :user OR f.receiver = :user")
    void deleteAllByUser(@Param("user") User user);
}