package com.h3.integration.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class H3AuditPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final String AUDIT_EXCHANGE = "audit.exchange";
    private static final String AUDIT_ROUTING_KEY = "audit.event";

    @Data
    @Builder
    public static class AuditEvent {
        private UUID userId;
        private String role;
        private String action;
        private String entityType;
        private UUID entityId;
        private String result;
        private String details;
    }

    public void publishEvent(UUID userId, String action,
            String entityType, UUID entityId, String result, String details) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .userId(userId)
                    .role("SYSTEM") // H3 service acts as SYSTEM
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .result(result)
                    .details(details)
                    .build();

            rabbitTemplate.convertAndSend(AUDIT_EXCHANGE, AUDIT_ROUTING_KEY, event);
            log.debug("Published audit event: {} for session {}", action, entityId);
        } catch (Exception e) {
            log.error("Failed to publish audit event for {}", entityId, e);
        }
    }
}
