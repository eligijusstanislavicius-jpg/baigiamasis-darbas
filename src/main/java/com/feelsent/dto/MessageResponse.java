package com.feelsent.dto;

import com.feelsent.enums.MessageStatus;
import com.feelsent.enums.Reaction;
import com.feelsent.enums.SendMode;
import com.feelsent.enums.WishTone;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private String senderFirstName;
    private Long receiverId;
    private String receiverUsername;
    private String receiverFirstName;

    private Long wishId;
    private String wishText;
    private WishTone wishTone;
    private String wishToneLabel;
    private String imageUrl;

    private SendMode sendMode;
    private String sendModeLabel;
    private MessageStatus status;
    private Boolean guessResult;
    private Reaction reaction;
    private String reactionLabel;
    private String reactionEmoji;

    private LocalDateTime sentAt;
    private boolean suggestReply;
}
