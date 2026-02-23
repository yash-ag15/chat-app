package com.yash.chat_app.user.dto;

public record UserEditRequest(
        String userName,
        String about
) {
}
