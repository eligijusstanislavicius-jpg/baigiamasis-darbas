package com.feelsent.service;

import com.feelsent.enums.PointReason;
import com.feelsent.model.Message;
import com.feelsent.model.PointTransaction;
import com.feelsent.model.User;
import com.feelsent.repository.PointTransactionRepository;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    // Taškų kiekiai pagal verslo taisykles
    private static final int POINTS_GUESS_CORRECT = 5;
    private static final int POINTS_REACTION_RECEIVED = 10;

    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;

    // Prideda 5 taškus siuntėjui kai gavėjas teisingai atspėjo toną
    public void awardGuessCorrect(User sender, Message message) {
        award(sender, message, POINTS_GUESS_CORRECT, PointReason.GUESS_CORRECT);
    }

    // Prideda 10 taškų siuntėjui kai gavėjas sureagavo į žinutę
    public void awardReactionReceived(User sender, Message message) {
        award(sender, message, POINTS_REACTION_RECEIVED, PointReason.REACTION_RECEIVED);
    }

    // Grąžina vartotojo taškų istoriją
    public List<PointTransaction> getHistory(User user) {
        return pointTransactionRepository.findAllByUser(user);
    }

    // @Transactional užtikrina atomą: arba abu įrašai išsaugomi, arba nei vienas
    // incrementPoints naudoja SQL UPDATE ... + :points – apsaugo nuo race condition
    @Transactional
    private void award(User user, Message message, int points, PointReason reason) {
        PointTransaction transaction = new PointTransaction();
        transaction.setUser(user);
        transaction.setMessage(message);
        transaction.setPoints(points);
        transaction.setReason(reason);
        transaction.setCreatedAt(LocalDateTime.now());
        pointTransactionRepository.save(transaction);

        userRepository.incrementPoints(user.getId(), points);
    }
}