package com.mockly.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @CreationTimestamp
    @Column(name = "action_time", nullable = false, updatable = false)
    private ZonedDateTime actionTime;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "action", length = 50, nullable = false)
    private String action;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "result", length = 20, nullable = false)
    private String result;

    @Column(name = "details", columnDefinition = "text")
    private String details;
}
