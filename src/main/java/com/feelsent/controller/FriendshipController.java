package com.feelsent.controller;

import com.feelsent.dto.AcceptRequest;
import com.feelsent.dto.FriendshipRequest;
import com.feelsent.dto.FriendshipResponse;
import com.feelsent.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    // POST /api/friendships/request – siunčia draugystės užklausą
    // Body: { "receiverId": 2, "relationshipType": "FRIEND" }
    @PostMapping("/request")
    public ResponseEntity<FriendshipResponse> sendRequest(@RequestBody FriendshipRequest request,
                                                          Authentication authentication) {
        FriendshipResponse response = friendshipService.sendRequest(
                authentication.getName(), // siuntėjo el. paštas iš JWT token'o
                request.getReceiverId(),
                request.getRelationshipType()
        );
        return ResponseEntity.ok(response);
    }

    // PATCH /api/friendships/{id}/accept – priima draugystės užklausą
    // Body: { "relationshipType": "FRIEND" } – gavėjas nurodo kas jam yra siuntėjas
    @PatchMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponse> acceptRequest(@PathVariable Long id,
                                                            @RequestBody AcceptRequest body,
                                                            Authentication authentication) {
        return ResponseEntity.ok(friendshipService.acceptRequest(authentication.getName(), id, body.getRelationshipType()));
    }

    // PATCH /api/friendships/{id}/decline – atmeta draugystės užklausą
    @PatchMapping("/{id}/decline")
    public ResponseEntity<FriendshipResponse> declineRequest(@PathVariable Long id,
                                                             Authentication authentication) {
        return ResponseEntity.ok(friendshipService.declineRequest(authentication.getName(), id));
    }

    // GET /api/friendships/pending – gautos laukiančios užklausos
    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipResponse>> getPendingRequests(Authentication authentication) {
        return ResponseEntity.ok(friendshipService.getPendingRequests(authentication.getName()));
    }

    // GET /api/friendships – visų draugų sąrašas (ACCEPTED)
    @GetMapping
    public ResponseEntity<List<FriendshipResponse>> getFriends(Authentication authentication) {
        return ResponseEntity.ok(friendshipService.getFriends(authentication.getName()));
    }

    // DELETE /api/friendships/{id} – ištrina draugystę (keičia statusą į REMOVED)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long id, Authentication authentication) {
        friendshipService.removeFriend(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}