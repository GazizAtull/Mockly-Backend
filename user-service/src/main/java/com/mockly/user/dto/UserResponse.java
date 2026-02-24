package com.mockly.user.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        String surname,
        String role,
        String avatarUrl,
        String level,
        String skills) {
}
