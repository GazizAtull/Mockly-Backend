package com.mockly.user.mapper;

import com.mockly.user.dto.UserResponse;
import com.mockly.user.entity.Profile;
import com.mockly.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user, Profile profile) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                profile.getName(),
                profile.getSurname(),
                profile.getRole() != null ? profile.getRole().name() : null,
                profile.getAvatarUrl(),
                profile.getLevel(),
                profile.getSkills()
        );
    }
}
