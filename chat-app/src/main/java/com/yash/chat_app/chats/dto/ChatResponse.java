package com.yash.chat_app.chats.dto;

import java.time.Instant;

public record ChatResponse(
        Long chatId,
        boolean isGroup,
        String chatName,
        Instant createdAt,
        String lastMessage,
        Instant lastMessageTime
) {
}
