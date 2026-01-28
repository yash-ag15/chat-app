package com.yash.chat_app.auth.dto;

public record RegistrationRequest(
        String username,
        String email,
        String password
) {
}
