package com.feelsent.service;

import com.feelsent.dto.WishResponse;
import com.feelsent.enums.FriendshipStatus;
import com.feelsent.enums.MoodWant;
import com.feelsent.enums.RelationshipType;
import com.feelsent.enums.WishTone;
import com.feelsent.model.*;
import com.feelsent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishServiceTest {

    @Mock WishRepository wishRepository;
    @Mock UserRepository userRepository;
    @Mock FriendshipRepository friendshipRepository;
    @Mock MessageRepository messageRepository;
    @Mock FavoriteWishRepository favoriteWishRepository;

    @InjectMocks
    WishService wishService;

    private User siuntejas;
    private User draugas;
    private Friendship draugyste;

    @BeforeEach
    void setUp() {
        siuntejas = new User();
        siuntejas.setId(1L);
        siuntejas.setEmail("jonas@test.lt");

        draugas = new User();
        draugas.setId(2L);
        draugas.setEmail("ona@test.lt");
        draugas.setMoodWant(null);

        draugyste = new Friendship();
        draugyste.setSender(siuntejas);
        draugyste.setReceiver(draugas);
        draugyste.setSenderRelationshipType(RelationshipType.FRIEND);
        draugyste.setStatus(FriendshipStatus.ACCEPTED);
    }

    private Wish sukurtiPalinkejiama(Long id, WishTone tonas, String rysys) {
        Wish w = new Wish();
        w.setId(id);
        w.setText("Palinkėjimas " + id);
        w.setTone(tonas);
        w.setRelationshipType(rysys);
        w.setActive(true);
        return w;
    }

    // --- suggestWishes() ---

    @Test
    void suggestWishes_grazina_nurodyto_kiekio_pasiulymus() {
        List<Wish> kandidatai = List.of(
                sukurtiPalinkejiama(1L, WishTone.SUPPORTIVE, "FRIEND"),
                sukurtiPalinkejiama(2L, WishTone.SUPPORTIVE, "FRIEND"),
                sukurtiPalinkejiama(3L, WishTone.SUPPORTIVE, "FRIEND")
        );

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(draugas));
        when(friendshipRepository.findFirstBySenderAndReceiverAndStatus(siuntejas, draugas, FriendshipStatus.ACCEPTED))
                .thenReturn(Optional.of(draugyste));
        when(wishRepository.findByToneAndRelationshipTypeOrAll(WishTone.SUPPORTIVE, "FRIEND"))
                .thenReturn(kandidatai);
        when(messageRepository.findAllBySenderAndReceiver(siuntejas, draugas)).thenReturn(List.of());
        when(messageRepository.findAllBySenderAndReceiver(draugas, siuntejas)).thenReturn(List.of());
        when(favoriteWishRepository.findAllByUser(siuntejas)).thenReturn(List.of());

        List<WishResponse> rezultatas = wishService.suggestWishes("jonas@test.lt", 2L, 3);

        assertThat(rezultatas).hasSize(3);
    }

    @Test
    void suggestWishes_nesiulo_jau_siustu_palinkejimu() {
        Wish jauSiustas = sukurtiPalinkejiama(1L, WishTone.SUPPORTIVE, "FRIEND");
        Wish naujas1 = sukurtiPalinkejiama(2L, WishTone.SUPPORTIVE, "FRIEND");
        Wish naujas2 = sukurtiPalinkejiama(3L, WishTone.SUPPORTIVE, "FRIEND");

        Message jauSiustaZinute = new Message();
        jauSiustaZinute.setWish(jauSiustas);

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(draugas));
        when(friendshipRepository.findFirstBySenderAndReceiverAndStatus(siuntejas, draugas, FriendshipStatus.ACCEPTED))
                .thenReturn(Optional.of(draugyste));
        when(wishRepository.findByToneAndRelationshipTypeOrAll(WishTone.SUPPORTIVE, "FRIEND"))
                .thenReturn(List.of(jauSiustas, naujas1, naujas2));
        when(messageRepository.findAllBySenderAndReceiver(siuntejas, draugas)).thenReturn(List.of(jauSiustaZinute));
        when(messageRepository.findAllBySenderAndReceiver(draugas, siuntejas)).thenReturn(List.of());
        when(favoriteWishRepository.findAllByUser(siuntejas)).thenReturn(List.of());

        List<WishResponse> rezultatas = wishService.suggestWishes("jonas@test.lt", 2L, 3);

        assertThat(rezultatas).noneMatch(r -> r.getId().equals(1L));
    }

    @Test
    void suggestWishes_nesiulo_megstamu_palinkejimu() {
        Wish megstamas = sukurtiPalinkejiama(1L, WishTone.SUPPORTIVE, "FRIEND");
        Wish kitas1 = sukurtiPalinkejiama(2L, WishTone.SUPPORTIVE, "FRIEND");
        Wish kitas2 = sukurtiPalinkejiama(3L, WishTone.SUPPORTIVE, "FRIEND");

        FavoriteWish megstamasIrasytas = new FavoriteWish();
        megstamasIrasytas.setWish(megstamas);
        megstamasIrasytas.setUser(siuntejas);

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(draugas));
        when(friendshipRepository.findFirstBySenderAndReceiverAndStatus(siuntejas, draugas, FriendshipStatus.ACCEPTED))
                .thenReturn(Optional.of(draugyste));
        when(wishRepository.findByToneAndRelationshipTypeOrAll(WishTone.SUPPORTIVE, "FRIEND"))
                .thenReturn(List.of(megstamas, kitas1, kitas2));
        when(messageRepository.findAllBySenderAndReceiver(siuntejas, draugas)).thenReturn(List.of());
        when(messageRepository.findAllBySenderAndReceiver(draugas, siuntejas)).thenReturn(List.of());
        when(favoriteWishRepository.findAllByUser(siuntejas)).thenReturn(List.of(megstamasIrasytas));

        List<WishResponse> rezultatas = wishService.suggestWishes("jonas@test.lt", 2L, 3);

        assertThat(rezultatas).noneMatch(r -> r.getId().equals(1L));
    }

    @Test
    void suggestWishes_cheer_me_up_nuotaika_siulo_funny_tona() {
        draugas.setMoodWant(MoodWant.CHEER_ME_UP);

        Wish funnyPalinkejiams = sukurtiPalinkejiama(5L, WishTone.FUNNY, "FRIEND");

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(draugas));
        when(friendshipRepository.findFirstBySenderAndReceiverAndStatus(siuntejas, draugas, FriendshipStatus.ACCEPTED))
                .thenReturn(Optional.of(draugyste));
        when(wishRepository.findByToneAndRelationshipTypeOrAll(WishTone.FUNNY, "FRIEND"))
                .thenReturn(List.of(funnyPalinkejiams));
        when(messageRepository.findAllBySenderAndReceiver(siuntejas, draugas)).thenReturn(List.of());
        when(messageRepository.findAllBySenderAndReceiver(draugas, siuntejas)).thenReturn(List.of());
        when(favoriteWishRepository.findAllByUser(siuntejas)).thenReturn(List.of());

        List<WishResponse> rezultatas = wishService.suggestWishes("jonas@test.lt", 2L, 1);

        assertThat(rezultatas).hasSize(1);
        verify(wishRepository).findByToneAndRelationshipTypeOrAll(eq(WishTone.FUNNY), anyString());
    }

    @Test
    void suggestWishes_nukrinta_i_2_lygi_kai_1_lygis_tuscias() {
        // 1 lygyje nieko nėra (tonas netinka), 2 lygyje yra pakankamai
        List<Wish> antrasisLygis = List.of(
                sukurtiPalinkejiama(10L, WishTone.ROMANTIC, "FRIEND"),
                sukurtiPalinkejiama(11L, WishTone.FUNNY, "FRIEND"),
                sukurtiPalinkejiama(12L, WishTone.BIRTHDAY, "FRIEND")
        );

        when(userRepository.findByEmail("jonas@test.lt")).thenReturn(Optional.of(siuntejas));
        when(userRepository.findById(2L)).thenReturn(Optional.of(draugas));
        when(friendshipRepository.findFirstBySenderAndReceiverAndStatus(siuntejas, draugas, FriendshipStatus.ACCEPTED))
                .thenReturn(Optional.of(draugyste));
        when(wishRepository.findByToneAndRelationshipTypeOrAll(WishTone.SUPPORTIVE, "FRIEND"))
                .thenReturn(List.of());
        when(wishRepository.findByRelationshipTypeAndActiveTrue("FRIEND"))
                .thenReturn(antrasisLygis);
        when(messageRepository.findAllBySenderAndReceiver(siuntejas, draugas)).thenReturn(List.of());
        when(messageRepository.findAllBySenderAndReceiver(draugas, siuntejas)).thenReturn(List.of());
        when(favoriteWishRepository.findAllByUser(siuntejas)).thenReturn(List.of());

        List<WishResponse> rezultatas = wishService.suggestWishes("jonas@test.lt", 2L, 3);

        assertThat(rezultatas).hasSize(3);
        verify(wishRepository).findByRelationshipTypeAndActiveTrue("FRIEND");
    }
}
