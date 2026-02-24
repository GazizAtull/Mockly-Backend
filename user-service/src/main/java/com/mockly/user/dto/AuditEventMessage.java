package com.mockly.user.dto;

import java.util.UUID;

public record AuditEventMessage(
        UUID userId,
        String role,
        AuditAction action,
        String entityType,
        UUID entityId,
        String result,
        String details
) {
}