package com.mockly.audit.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class KeycloakJwtUtil {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Keycloak обычно хранит UUID пользователя в claim "sub" (subject)
            // Если у вас он лежит в другом месте (например, "user_id"), замените getSubject() на jwt.getClaimAsString("user_id")
            String userId = jwt.getSubject();
            return UUID.fromString(userId);
        }

        throw new IllegalStateException("Пользователь не авторизован или токен недействителен");
    }
    public List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null) {
            return List.of();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replaceFirst("^ROLE_", "").toUpperCase()) // Убираем ROLE_ для удобства проверок
                .toList();
    }
}
