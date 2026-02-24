package com.mockly.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditEvent {
        private UUID userId;
        private String role;
        private String action; // Было AuditAction, стало String для гибкости
        private String entityType;
        private UUID entityId;
        private String result;
        private String details;
    }

    public record AuditLogResponse(
            UUID id,
            ZonedDateTime actionTime,
            UUID userId,
            String role,
            String action,
            String entityType,
            UUID entityId,
            String result,
            String details) {
    }
}