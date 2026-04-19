package com.feelsent.controller;

import com.feelsent.dto.MessageLimitResponse;
import com.feelsent.service.MessageLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message-limits")
@RequiredArgsConstructor
public class MessageLimitController {

    private final MessageLimitService messageLimitService;

    // POST /api/message-limits – nustato arba atnaujina limitą siuntėjui
    // Body: { "senderId": 2, "dailyLimit": 3 }
    @PostMapping
    public ResponseEntity<MessageLimitResponse> setLimit(@RequestBody Map<String, Object> body,
                                                         Authentication authentication) {
        Long senderId = Long.valueOf(body.get("senderId").toString());
        int dailyLimit = Integer.parseInt(body.get("dailyLimit").toString());
        return ResponseEntity.ok(messageLimitService.setLimit(authentication.getName(), senderId, dailyLimit));
    }

    // DELETE /api/message-limits/{senderId} – pašalina limitą (siuntėjas vėl gali siųsti neribotai)
    @DeleteMapping("/{senderId}")
    public ResponseEntity<Void> removeLimit(@PathVariable Long senderId,
                                            Authentication authentication) {
        messageLimitService.removeLimit(authentication.getName(), senderId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/message-limits – visi šio vartotojo nustatyti limitai (kieno siuntimą apribojau)
    @GetMapping
    public ResponseEntity<List<MessageLimitResponse>> getMyLimits(Authentication authentication) {
        return ResponseEntity.ok(messageLimitService.getMyLimits(authentication.getName()));
    }
}