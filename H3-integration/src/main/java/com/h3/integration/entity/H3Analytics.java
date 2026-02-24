package com.h3.integration.entity;

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
@Table(name = "h3_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class H3Analytics {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "h3_index", length = 20, nullable = false)
    private String h3Index;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "avg_latency_ms")
    private Integer avgLatencyMs;

    @Column(name = "packet_loss_percent")
    private Double packetLossPercent;

    @Column(name = "disconnect_count")
    private Integer disconnectCount;

    @Column(name = "quality_score")
    private Double qualityScore;

    @Column(name = "fallback_recommended")
    private Boolean fallbackRecommended;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
}
