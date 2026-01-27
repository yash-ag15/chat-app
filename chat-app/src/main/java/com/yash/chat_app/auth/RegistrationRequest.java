package com.yash.chat_app.auth;

public record RegistrationRequest(
        String username,
        String email,
        String password
) {
}
