package com.mockly.audit.controller.api;

import com.mockly.audit.dto.AuditDto.AuditLogResponse;
import com.mockly.audit.service.AuditService;
import com.mockly.audit.utils.KeycloakJwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;
    private final KeycloakJwtUtil keycloakJwtUtils;

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<String> roles = keycloakJwtUtils.getUserRoles();
        if (!roles.contains("ADMIN")) {
            throw new AccessDeniedException("Только администраторы могут просматривать общий журнал аудита");
        }

        return ResponseEntity.ok(auditService.getAuditLogs(page, size));
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<AuditLogResponse>> getLogsByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID currentUserId = keycloakJwtUtils.getCurrentUserId();
        List<String> roles = keycloakJwtUtils.getUserRoles();

        if (!currentUserId.equals(userId) && !roles.contains("ADMIN")) {
            throw new AccessDeniedException("Недостаточно прав для просмотра чужих логов аудита");
        }

        return ResponseEntity.ok(auditService.getAuditLogsByUserId(userId, page, size));
    }
}
