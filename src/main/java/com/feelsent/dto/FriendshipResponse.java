package com.feelsent.dto;

import com.feelsent.enums.FriendshipStatus;
import com.feelsent.enums.MoodStatus;
import com.feelsent.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FriendshipResponse {
    private Long id;
    private Long senderId;
    private String senderFirstName;
    private MoodStatus senderMoodStatus;
    private String senderMoodStatusLabel;
    private Long receiverId;
    private String receiverFirstName;
    private MoodStatus receiverMoodStatus;
    private String receiverMoodStatusLabel;
    private RelationshipType senderRelationshipType;
    private String senderRelationshipTypeLabel;
    private RelationshipType receiverRelationshipType;
    private String receiverRelationshipTypeLabel;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
}
