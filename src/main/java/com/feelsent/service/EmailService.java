package com.feelsent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// @Slf4j – automatiškai sukuria log kintamąjį klaidų registravimui
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Pasveikinimo laiškas po registracijos
    @Async // siunčiama fone – vartotojas nelaukia kol laiškas išsiųstas
    public void sendWelcomeEmail(String toEmail, String username) {
        send(
                toEmail,
                "Sveiki atvykę į Glebis!",
                "Sveiki, " + username + "!\n\n" +
                "Džiaugiamės, kad prisijungėte prie Glebis – vietos, kur galite dalintis\n" +
                "šiltais palinkėjimais su artimais žmonėmis.\n\n" +
                "Pradėkite: pridėkite draugą ir nusiųskite pirmą palinkėjimą!\n\n" +
                "Glebis komanda"
        );
    }

    // Pranešimas kai kas nors siunčia draugystės užklausą
    @Async
    public void sendFriendRequestEmail(String toEmail, String receiverUsername, String senderUsername) {
        send(
                toEmail,
                senderUsername + " nori susipažinti!",
                "Sveiki, " + receiverUsername + "!\n\n" +
                senderUsername + " išsiuntė jums draugystės užklausą Glebis platformoje.\n\n" +
                "Prisijunkite ir priimkite arba atmeskite užklausą.\n\n" +
                "Glebis komanda"
        );
    }

    // Priminimas neaktyviems vartotojams turintiems neperskaitytų pranešimų
    @Async
    public void sendReEngagementEmail(String toEmail, String username) {
        send(
                toEmail,
                "Jūsų laukia neperskaityti pranešimai!",
                "Sveiki, " + username + "!\n\n" +
                "Jūsų Glebis paskyroje laukia neperskaityti pranešimai.\n\n" +
                "Prisijunkite ir peržiūrėkite kas naujo!\n\n" +
                "Glebis komanda"
        );
    }

    // Bendrinis siuntimo metodas – klaidos nepertraukia pagrindinio srauto
    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("El. laiškas išsiųstas: {}", to);
        } catch (Exception e) {
            // El. pašto klaida neturėtų sustabdyti pagrindinės operacijos
            log.error("Nepavyko išsiųsti el. laiško į {}: {}", to, e.getMessage());
        }
    }
}