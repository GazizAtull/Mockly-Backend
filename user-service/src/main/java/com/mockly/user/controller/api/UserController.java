package com.mockly.user.controller.api;

import com.mockly.user.dto.UpdateProfileRequest;
import com.mockly.user.dto.UserResponse;
import com.mockly.user.service.UserService;
import com.mockly.user.utils.KeycloakJwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KeycloakJwtUtils keycloakJwtUtils;

    @GetMapping("/me")
    public UserResponse getMe() {
        UUID userId = keycloakJwtUtils.getCurrentUserId();
        return userService.getUserById(userId);
    }

    @PatchMapping("/me/profile")
    public UserResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UUID userId = keycloakJwtUtils.getCurrentUserId();
        return userService.updateProfile(userId, request);
    }

    @GetMapping("/admin/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/admin/by-email")
    public UserResponse getByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }
}
