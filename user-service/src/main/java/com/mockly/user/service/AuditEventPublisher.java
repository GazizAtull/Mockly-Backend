package com.mockly.user.service;

import com.mockly.user.dto.AuditAction;
import com.mockly.user.dto.AuditEventMessage;
import com.mockly.user.dto.AuditProperties;
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
    private final AuditProperties auditProperties;

    public void publishEvent(UUID userId, String role, AuditAction action,
                             String entityType, UUID entityId, String result, String details) {
        try {
            AuditEventMessage event = new AuditEventMessage(
                    userId,
                    role,
                    action,
                    entityType,
                    entityId,
                    result,
                    details
            );

            rabbitTemplate.convertAndSend(
                    auditProperties.getExchange(),
                    auditProperties.getRoutingKey(),
                    event
            );

            log.debug("Published audit event: action={}, userId={}", action, userId);
        } catch (Exception e) {
            log.error("Failed to publish audit event for userId={}", userId, e);
        }
    }
}
