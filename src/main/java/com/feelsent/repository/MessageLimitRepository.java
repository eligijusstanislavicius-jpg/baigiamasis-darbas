package com.feelsent.repository;

import com.feelsent.model.MessageLimit;
import com.feelsent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageLimitRepository extends JpaRepository<MessageLimit, Long> {

    Optional<MessageLimit> findFirstByReceiverAndSender(User receiver, User sender); // ieško konkretaus limito

    boolean existsByReceiverAndSender(User receiver, User sender); // tikrina ar limitas jau nustatytas

    List<MessageLimit> findAllByReceiver(User receiver); // visi gavėjo nustatyti limitai

    @Modifying
    @Query("DELETE FROM MessageLimit ml WHERE ml.sender = :user OR ml.receiver = :user")
    void deleteAllByUser(@Param("user") User user);
}
