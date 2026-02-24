package com.mockly.user.service;

import com.mockly.user.dto.AuditAction;
import com.mockly.user.dto.UpdateProfileRequest;
import com.mockly.user.dto.UserResponse;
import com.mockly.user.entity.Profile;
import com.mockly.user.entity.User;
import com.mockly.user.exception.ResourceNotFoundException;
import com.mockly.user.exception.UserNotFoundException;
import com.mockly.user.repository.ProfileRepository;
import com.mockly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final AuditEventPublisher auditEventPublisher;

    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                profile.getName(),
                profile.getSurname(),
                profile.getRole(),
                profile.getAvatarUrl(),
                profile.getLevel(),
                profile.getSkills());
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + user.getId()));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                profile.getName(),
                profile.getSurname(),
                profile.getRole(),
                profile.getAvatarUrl(),
                profile.getLevel(),
                profile.getSkills());
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));

        if (request.name() != null) {
            profile.setName(request.name());
        }
        if (request.surname() != null) {
            profile.setSurname(request.surname());
        }
        if (request.avatarUrl() != null) {
            profile.setAvatarUrl(request.avatarUrl());
        }
        if (request.level() != null) {
            profile.setLevel(request.level());
        }

        profile = profileRepository.save(profile);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        // Emit audit event
        auditEventPublisher.publishEvent(
                userId,
                profile.getRole(),
                AuditAction.PROFILE_UPDATED,
                "PROFILE",
                userId,
                "SUCCESS",
                "User profile updated successfully.");

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                profile.getName(),
                profile.getSurname(),
                profile.getRole(),
                profile.getAvatarUrl(),
                profile.getLevel(),
                profile.getSkills());
    }
}
