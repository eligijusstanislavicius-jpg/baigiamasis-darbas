package com.feelsent.service;

import com.feelsent.model.User;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private static final int POINTS_GUESS_CORRECT = 5;
    private static final int POINTS_GUESS_CORRECT_RECEIVER = 3;
    private static final int POINTS_REACTION_RECEIVED = 10;

    private final UserRepository userRepository;

    // 5 taškai siuntėjui kai gavėjas teisingai atspėjo toną
    @Transactional
    public void awardGuessCorrect(User sender) {
        userRepository.incrementPoints(sender.getId(), POINTS_GUESS_CORRECT);
    }

    // 3 taškai gavėjui kai jis teisingai atspėjo toną
    @Transactional
    public void awardGuessCorrectForReceiver(User receiver) {
        userRepository.incrementPoints(receiver.getId(), POINTS_GUESS_CORRECT_RECEIVER);
    }

    // 10 taškų siuntėjui kai gavėjas sureagavo į žinutę
    @Transactional
    public void awardReactionReceived(User sender) {
        userRepository.incrementPoints(sender.getId(), POINTS_REACTION_RECEIVED);
    }
}
