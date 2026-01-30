package com.yash.chat_app.friends.dto;

import com.yash.chat_app.friends.entity.FriendRequestStatus;

import java.time.Instant;

public record FriendRequestResponse(
        Long requestId,
        Long senderId,
        String senderName,
        Long receiverId,
        FriendRequestStatus status,
        Instant createdAt
) {
}
