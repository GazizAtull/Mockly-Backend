package com.mockly.user.service;

import com.mockly.user.dto.AuditAction;
import com.mockly.user.dto.UpdateProfileRequest;
import com.mockly.user.dto.UserResponse;
import com.mockly.user.entity.Profile;
import com.mockly.user.entity.User;
import com.mockly.user.exception.ResourceNotFoundException;
import com.mockly.user.exception.UserNotFoundException;
import com.mockly.user.mapper.UserMapper;
import com.mockly.user.repository.ProfileRepository;
import com.mockly.user.repository.UserRepository;
import com.mockly.user.utils.KeycloakJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final AuditEventPublisher auditEventPublisher;
    private final UserMapper userMapper;
    private final KeycloakJwtUtils keycloakJwtUtils;

    public UserResponse getUserById(UUID userId) {
        UUID currentUserId = keycloakJwtUtils.getCurrentUserId();
        List<String> roles = keycloakJwtUtils.getUserRoles();

        if (!currentUserId.equals(userId) && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("Недостаточно прав для просмотра профиля");
        }

        User user = getUserOrThrow(userId);
        Profile profile = getProfileOrThrow(userId);
        return userMapper.toResponse(user, profile);
    }

    public UserResponse getUserByEmail(String email) {
        List<String> roles = keycloakJwtUtils.getUserRoles();

        if (!roles.contains("ADMIN")) {
            throw new AccessDeniedException("Поиск по email доступен только администраторам");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Profile profile = getProfileOrThrow(user.getId());
        return userMapper.toResponse(user, profile);
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        UUID currentUserId = keycloakJwtUtils.getCurrentUserId();
        List<String> roles = keycloakJwtUtils.getUserRoles();

        if (!currentUserId.equals(userId) && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("Недостаточно прав для редактирования профиля");
        }

        User user = getUserOrThrow(userId);
        Profile profile = getProfileOrThrow(userId);

        applyPatch(profile, request);

        profileRepository.save(profile);

        auditEventPublisher.publishEvent(
                userId,
                profile.getRole() != null ? profile.getRole().name() : null,
                AuditAction.PROFILE_UPDATED,
                "PROFILE",
                userId,
                "SUCCESS",
                "User profile updated successfully"
        );

        return userMapper.toResponse(user, profile);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Profile getProfileOrThrow(UUID userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
    }

    private void applyPatch(Profile profile, UpdateProfileRequest request) {
        if (request == null) {
            return;
        }

        if (request.name() != null) {
            profile.setName(normalize(request.name()));
        }
        if (request.surname() != null) {
            profile.setSurname(normalize(request.surname()));
        }
        if (request.avatarUrl() != null) {
            profile.setAvatarUrl(normalize(request.avatarUrl()));
        }
        if (request.level() != null) {
            profile.setLevel(normalize(request.level()));
        }
    }

    private String normalize(String value) {
        String trimmed = value == null ? null : value.trim();
        return (trimmed == null || trimmed.isEmpty()) ? null : trimmed;
    }
}