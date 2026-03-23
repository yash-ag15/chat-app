package com.yash.chat_app.chats.dto;

public record Presence(
        String userName,
        boolean online
) {
}
