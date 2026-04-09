package com.yash.chat_app.user.dto;

public record UserProfileResponse(

        Long id,
        String userName,
        String about,
        String profilePhotoUrl
) {
}
