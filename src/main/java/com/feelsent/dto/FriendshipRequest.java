package com.feelsent.dto;

import com.feelsent.enums.RelationshipType;
import lombok.Data;

// Duomenys siųsti draugystės užklausą: gavėjo ID ir ryšio tipas
@Data
public class FriendshipRequest {
    private Long receiverId;
    private RelationshipType relationshipType;
}