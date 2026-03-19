package com.yash.chat_app.chats.dto;

import java.util.List;

public record CreateGroupRequest(
        String chatName,
        List<String>memberUserNames
) {
}
