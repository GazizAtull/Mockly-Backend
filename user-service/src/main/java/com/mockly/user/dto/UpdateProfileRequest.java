package com.mockly.user.dto;

public record UpdateProfileRequest(
        String name,
        String surname,
        String avatarUrl,
        String level) {
}
