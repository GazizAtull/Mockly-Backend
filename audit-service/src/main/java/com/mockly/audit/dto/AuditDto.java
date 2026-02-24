package com.mockly.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditDto {

    public enum AuditAction {
        PROFILE_UPDATED,
        USER_LOGGED_IN,
        USER_REGISTERED,
        H3_ANALYTICS_RECORDED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditEvent {
        private UUID userId;
        private String role;
        private AuditAction action;
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
