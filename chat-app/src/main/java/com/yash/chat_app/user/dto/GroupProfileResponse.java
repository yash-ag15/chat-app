package com.yash.chat_app.user.dto;

import java.util.List;

public record GroupProfileResponse(
        Long chatId,
        String chatName,
        int memberCount,
        String groupPhotoUrl,
        List<GroupMemberDto>members
) {
}
