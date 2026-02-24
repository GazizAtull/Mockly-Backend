package com.mockly.user.service;

import com.mockly.user.dto.AuditAction;
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
public class AuditEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final String AUDIT_EXCHANGE = "audit.exchange";
    private static final String AUDIT_ROUTING_KEY = "audit.event";

    @Data
    @Builder
    public static class AuditEvent {
        private UUID userId;
        private String role;
        private AuditAction action;
        private String entityType;
        private UUID entityId;
        private String result;
        private String details;
    }

    public void publishEvent(UUID userId, String role, AuditAction action,
            String entityType, UUID entityId, String result, String details) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .userId(userId)
                    .role(role)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .result(result)
                    .details(details)
                    .build();

            // Fire and forget event to RabbitMQ
            rabbitTemplate.convertAndSend(AUDIT_EXCHANGE, AUDIT_ROUTING_KEY, event);
            log.debug("Published audit event: {} for {}", action, userId);
        } catch (Exception e) {
            log.error("Failed to publish audit event for {}", userId, e);
        }
    }
}
