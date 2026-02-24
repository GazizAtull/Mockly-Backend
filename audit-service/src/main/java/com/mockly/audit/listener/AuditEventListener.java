package com.mockly.audit.listener;

import com.mockly.audit.dto.AuditDto.AuditEvent;
import com.mockly.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditService auditService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "audit.queue", durable = "true"), exchange = @Exchange(value = "audit.exchange", type = "topic"), key = "audit.event"))
    public void handleAuditEvent(AuditEvent event) {
        log.info("Received audit event for user: {}", event.getUserId());
        try {
            auditService.log(
                    event.getUserId(),
                    event.getRole(),
                    event.getAction(),
                    event.getEntityType(),
                    event.getEntityId(),
                    event.getResult(),
                    event.getDetails());
        } catch (Exception e) {
            log.error("Failed to process audit event from queue: {}", e.getMessage(), e);
        }
    }
}
