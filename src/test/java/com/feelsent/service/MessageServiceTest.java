package com.feelsent.service;

import com.feelsent.dto.MessageResponse;
import com.feelsent.dto.SendMessageRequest;
import com.feelsent.enums.*;
import com.feelsent.exception.MessageLimitExceededException;
import com.feelsent.exception.NotFriendsException;
import com.feelsent.model.*;
import com.feelsent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock UserRepository userRepository;
    @Mock WishRepository wishRepository;
    @Mock UniqueWishRepository uniqueWishRepository;
    @Mock MessageLimitRepository messageLimitRepository;
    @Mock FriendshipService friendshipService;
    @Mock PointService pointService;
    @Mock EmailService emailService;
    @Mock NotificationService notificationService;

    @InjectMocks
    MessageService messageService;

    private User siuntejas;
    private User gavejas;
    private Wish palinkejiams;
    private Message zinute;

    @BeforeEach
    void setUp() {
        siuntejas = new User();
        siuntejas.setId(1L);
        siuntejas.setFirstName("Jonas");
        siuntejas.setEmail("jonas@test.lt");
        siuntejas.setRole(Role.USER);
        siuntejas.setPoints(0);

        gavejas = new User();
        gavejas.setId(2L);
        gavejas.setFirstName("Ona");
        gavejas.setEmail("ona@test.lt");
        gavejas.setRole(Role.USER);
        gavejas.setPoints(0);

        palinkejiams = new Wish();
        palinkejiams.setId(10L);
        palinkejiams.setText("Linkiu geros dienos!");
        palinkejiams.setTone(WishTone.SUPPORTIVE);
        palinkejiams.setRelationshipType("FRIEND");
        palinkejiams.setActive(true);

        zinute = new Message();
        zinute.setId(100L);
        zinute.setSender(siuntejas);
        zinute.setReceiver(gavejas);
        zinute.setWish(palinkejiams);
        zinute.setSendMode(SendMode.SIMPLE);
        zinute.setStatus(MessageStatus.SENT);
        zinute.setSentAt(LocalDateTime.now());
    }

    // --- send() ---

    @Test
    void send_meta_klaida_kai_ne_draugai() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setWishId(10L);
        request.setSendMode(SendMode.SIMPLE);

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(gavejas));
        when(friendshipService.areFriends(siuntejas, gavejas)).thenReturn(false);

        assertThatThrownBy(() -> messageService.send("jonas@test.lt", request))
                .isInstanceOf(NotFriendsException.class);
    }

    @Test
    void send_meta_klaida_kai_virsytas_limitas() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setWishId(10L);
        request.setSendMode(SendMode.SIMPLE);

        MessageLimit limitas = new MessageLimit();
        limitas.setReceiver(gavejas);
        limitas.setSender(siuntejas);
        limitas.setDailyLimit(1);

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(gavejas));
        when(friendshipService.areFriends(siuntejas, gavejas)).thenReturn(true);
        when(messageLimitRepository.findFirstByReceiverAndSender(gavejas, siuntejas))
                .thenReturn(Optional.of(limitas));
        when(messageRepository.countBySenderAndReceiverAndSentAtAfter(eq(siuntejas), eq(gavejas), any()))
                .thenReturn(1L);

        assertThatThrownBy(() -> messageService.send("jonas@test.lt", request))
                .isInstanceOf(MessageLimitExceededException.class);
    }

    @Test
    void send_sekmingai_issiuncia_zinute() {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(2L);
        request.setWishId(10L);
        request.setSendMode(SendMode.SIMPLE);

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(gavejas));
        when(friendshipService.areFriends(siuntejas, gavejas)).thenReturn(true);
        when(messageLimitRepository.findFirstByReceiverAndSender(gavejas, siuntejas)).thenReturn(Optional.empty());
        when(wishRepository.findById(10L)).thenReturn(Optional.of(palinkejiams));
        when(messageRepository.save(any())).thenReturn(zinute);

        MessageResponse result = messageService.send("jonas@test.lt", request);

        assertThat(result).isNotNull();
        assertThat(result.getSenderFirstName()).isEqualTo("Jonas");
        verify(notificationService).create(eq(gavejas), eq(NotificationType.NEW_MESSAGE), anyString(), anyLong());
    }

    // --- openMessage() ---

    @Test
    void openMessage_keicia_statusa_i_opened() {
        when(messageRepository.findById(100L)).thenReturn(Optional.of(zinute));
        when(messageRepository.save(any())).thenReturn(zinute);

        MessageResponse result = messageService.openMessage("ona@test.lt", 100L);

        assertThat(result).isNotNull();
        assertThat(zinute.getStatus()).isEqualTo(MessageStatus.OPENED);
    }

    @Test
    void openMessage_meta_klaida_kai_jau_atidaryta() {
        zinute.setStatus(MessageStatus.OPENED);
        when(messageRepository.findById(100L)).thenReturn(Optional.of(zinute));

        assertThatThrownBy(() -> messageService.openMessage("ona@test.lt", 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jau atidaryta");
    }

    // --- guessWish() ---

    @Test
    void guessWish_teisingas_atspejimas_skiria_taskus() {
        zinute.setSendMode(SendMode.GUESS);
        zinute.setStatus(MessageStatus.OPENED);
        when(messageRepository.findById(100L)).thenReturn(Optional.of(zinute));
        when(messageRepository.save(any())).thenReturn(zinute);

        MessageResponse result = messageService.guessWish("ona@test.lt", 100L, WishTone.SUPPORTIVE);

        assertThat(zinute.getGuessResult()).isTrue();
        assertThat(zinute.getStatus()).isEqualTo(MessageStatus.GUESSED);
        verify(pointService).awardGuessCorrect(siuntejas);
        verify(pointService).awardGuessCorrectForReceiver(gavejas);
        verify(notificationService, times(2)).create(any(), eq(NotificationType.GUESS_CORRECT), anyString(), any());
    }

    @Test
    void guessWish_neteisingas_atspejimas_neskiria_tasku() {
        zinute.setSendMode(SendMode.GUESS);
        zinute.setStatus(MessageStatus.OPENED);
        when(messageRepository.findById(100L)).thenReturn(Optional.of(zinute));
        when(messageRepository.save(any())).thenReturn(zinute);

        messageService.guessWish("ona@test.lt", 100L, WishTone.FUNNY);

        assertThat(zinute.getGuessResult()).isFalse();
        verify(pointService, never()).awardGuessCorrect(any());
        verify(pointService, never()).awardGuessCorrectForReceiver(any());
    }

    @Test
    void guessWish_meta_klaida_jei_ne_guess_rezimas() {
        zinute.setSendMode(SendMode.SIMPLE);
        zinute.setStatus(MessageStatus.OPENED);
        when(messageRepository.findById(100L)).thenReturn(Optional.of(zinute));

        assertThatThrownBy(() -> messageService.guessWish("ona@test.lt", 100L, WishTone.SUPPORTIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GUESS");
    }

    // --- reactToMessage() ---

    @Test
    void reactToMessage_sekmingai_sureaguoja_ir_skiria_taskus() {
        zinute.setStatus(MessageStatus.OPENED);
        when(messageRepository.findById(100L)).thenReturn(Optional.of(zinute));
        when(messageRepository.save(any())).thenReturn(zinute);

        MessageResponse result = messageService.reactToMessage("ona@test.lt", 100L, Reaction.CHEERED_UP);

        assertThat(zinute.getStatus()).isEqualTo(MessageStatus.REACTED);
        assertThat(zinute.getReaction()).isEqualTo(Reaction.CHEERED_UP);
        verify(pointService).awardReactionReceived(siuntejas);
        verify(notificationService, times(2)).create(any(), eq(NotificationType.MESSAGE_REACTED), anyString(), any());
    }

    @Test
    void reactToMessage_meta_klaida_kai_zinute_dar_neatidaryta() {
        zinute.setStatus(MessageStatus.SENT);
        when(messageRepository.findById(100L)).thenReturn(Optional.of(zinute));

        assertThatThrownBy(() -> messageService.reactToMessage("ona@test.lt", 100L, Reaction.COMFORTED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("atidaryta");
    }
}
