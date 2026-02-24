package com.mockly.audit.service;

import com.mockly.audit.dto.AuditDto.AuditAction;
import com.mockly.audit.dto.AuditDto.AuditLogResponse;
import com.mockly.audit.entity.AuditLog;
import com.mockly.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void log(UUID userId, String role, AuditAction action,
            String entityType, UUID entityId, String result, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .role(role)
                    .action(action.name())
                    .entityType(entityType)
                    .entityId(entityId)
                    .result(result)
                    .details(details)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log recorded: {} {} on {} {}", action, result, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to record audit log: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findAllByOrderByActionTimeDesc(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByUserId(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findByUserIdOrderByActionTimeDesc(userId, pageable)
                .map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog entity) {
        return new AuditLogResponse(
                entity.getId(),
                entity.getActionTime(),
                entity.getUserId(),
                entity.getRole(),
                entity.getAction(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getResult(),
                entity.getDetails());
    }
}
