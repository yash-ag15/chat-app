package com.yash.chat_app.chats.dto;

public record Typing(
        Long chatId,
        String senderName,
        boolean isTyping
) {
}
