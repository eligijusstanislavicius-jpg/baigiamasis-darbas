package com.feelsent.controller;

import com.feelsent.dto.MessageResponse;
import com.feelsent.dto.SendMessageRequest;
import com.feelsent.enums.Reaction;
import com.feelsent.enums.WishTone;
import com.feelsent.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // POST /api/messages – siunčia žinutę draugui
    // Body: { "receiverId": 2, "wishId": 5, "sendMode": "GUESS" }
    @PostMapping
    public ResponseEntity<MessageResponse> send(@RequestBody SendMessageRequest request,
                                                Authentication authentication) {
        return ResponseEntity.ok(messageService.send(authentication.getName(), request));
    }

    // PATCH /api/messages/{id}/open – gavėjas atidaro žinutę (SENT → OPENED)
    @PatchMapping("/{id}/open")
    public ResponseEntity<MessageResponse> open(@PathVariable Long id,
                                                Authentication authentication) {
        return ResponseEntity.ok(messageService.openMessage(authentication.getName(), id));
    }

    // PATCH /api/messages/{id}/guess – gavėjas spėja toną (tik GUESS režimas)
    // Body: { "tone": "FUNNY" }
    @PatchMapping("/{id}/guess")
    public ResponseEntity<MessageResponse> guess(@PathVariable Long id,
                                                 @RequestBody Map<String, String> body,
                                                 Authentication authentication) {
        String toneValue = body.get("tone");
        if (toneValue == null) {
            throw new IllegalArgumentException("Laukas 'tone' yra privalomas");
        }
        WishTone tone;
        try {
            tone = WishTone.valueOf(toneValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nežinomas tonas: " + toneValue);
        }
        return ResponseEntity.ok(messageService.guessWish(authentication.getName(), id, tone));
    }

    // PATCH /api/messages/{id}/react – gavėjas reaguoja į žinutę
    // Body: { "reaction": "WARMED_UP" }
    @PatchMapping("/{id}/react")
    public ResponseEntity<MessageResponse> react(@PathVariable Long id,
                                                 @RequestBody Map<String, String> body,
                                                 Authentication authentication) {
        String reactionValue = body.get("reaction");
        if (reactionValue == null) {
            throw new IllegalArgumentException("Laukas 'reaction' yra privalomas");
        }
        Reaction reaction;
        try {
            reaction = Reaction.valueOf(reactionValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nežinoma reakcija: " + reactionValue);
        }
        return ResponseEntity.ok(messageService.reactToMessage(authentication.getName(), id, reaction));
    }

    // GET /api/messages/reactions – galimų reakcijų sąrašas su emoji
    // Kliento pusės kešas (CacheControl.maxAge): naršyklė po pirmo užkrovimo
    // 24 val. nebesikreipia į serverį – reakcijų sąrašas nesikeičia tarp deploy'ų
    @GetMapping("/reactions")
    public ResponseEntity<List<Map<String, String>>> getReactions() {
        List<Map<String, String>> reactions = Arrays.stream(Reaction.values())
                .map(r -> Map.of("value", r.name(), "emoji", r.getEmoji(), "label", r.getLabel()))
                .toList();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .body(reactions);
    }

    // GET /api/messages/inbox – gautos žinutės (naujausios pirma)
    @GetMapping("/inbox")
    public ResponseEntity<List<MessageResponse>> inbox(Authentication authentication) {
        return ResponseEntity.ok(messageService.getInbox(authentication.getName()));
    }

    // GET /api/messages/sent – išsiųstos žinutės
    @GetMapping("/sent")
    public ResponseEntity<List<MessageResponse>> sent(Authentication authentication) {
        return ResponseEntity.ok(messageService.getSent(authentication.getName()));
    }
}