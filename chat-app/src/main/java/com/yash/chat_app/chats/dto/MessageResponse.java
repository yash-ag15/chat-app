package com.yash.chat_app.chats.dto;

import java.time.Instant;

public record MessageResponse(
        Long messageId,
        String senderName,
        String content,
        Instant sentAt,
        Long chatId,

        String imageUrl
) {
}
