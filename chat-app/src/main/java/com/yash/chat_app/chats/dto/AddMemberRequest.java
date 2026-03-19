package com.yash.chat_app.chats.dto;

import java.util.List;

public record AddMemberRequest(

        List<String> memberUserNames
) {
}
