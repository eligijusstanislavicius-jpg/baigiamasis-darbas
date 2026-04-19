package com.feelsent.repository;

import com.feelsent.model.MessageLimit;
import com.feelsent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageLimitRepository extends JpaRepository<MessageLimit, Long> {

    Optional<MessageLimit> findByReceiverAndSender(User receiver, User sender); // ieško konkretaus limito

    boolean existsByReceiverAndSender(User receiver, User sender); // tikrina ar limitas jau nustatytas

    List<MessageLimit> findAllByReceiver(User receiver); // visi gavėjo nustatyti limitai
}
