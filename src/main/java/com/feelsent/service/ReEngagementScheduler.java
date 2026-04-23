package com.feelsent.service;

import com.feelsent.enums.MessageStatus;
import com.feelsent.enums.NotificationType;
import com.feelsent.model.Message;
import com.feelsent.model.User;
import com.feelsent.repository.MessageRepository;
import com.feelsent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReEngagementScheduler {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    // Kiekvieną dieną 10:00 tikrina neaktyvius vartotojus su neperskaitytais pranešimais
    @Scheduled(cron = "0 0 10 * * *")
    public void sendReEngagementEmails() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        List<User> inactiveUsers = userRepository.findAllByLastLoginAtBefore(threshold, PageRequest.of(0, 500));

        for (User user : inactiveUsers) {
            if (notificationService.hasUnread(user)) {
                emailService.sendReEngagementEmail(user.getEmail(), user.getUsername());
                log.info("Re-engagement laiškas išsiųstas: {}", user.getEmail());
            }
        }
    }

    // Kas valandą tikrina žinutes kurios liko neatsakytos ilgiau nei 2 dienas
    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void expireOldMessages() {
        LocalDateTime expiryThreshold = LocalDateTime.now().minusDays(2);
        List<MessageStatus> alreadyDone = Arrays.asList(MessageStatus.REACTED, MessageStatus.EXPIRED);

        List<Message> toExpire = messageRepository.findAllByStatusNotInAndSentAtBefore(alreadyDone, expiryThreshold);

        for (Message message : toExpire) {
            message.setStatus(MessageStatus.EXPIRED);
            messageRepository.save(message);

            String receiverFullName = message.getReceiver().getFirstName() + " "
                    + message.getReceiver().getLastName();

            notificationService.create(
                    message.getSender(),
                    NotificationType.MESSAGE_EXPIRED,
                    "Į jūsų palinkėjimą " + receiverFullName + " nesureagavo — žinutė ištrinta",
                    message.getId()
            );

            log.info("Žinutė {} paversta EXPIRED, pranešimas išsiųstas siuntėjui {}",
                    message.getId(), message.getSender().getEmail());
        }
    }
}