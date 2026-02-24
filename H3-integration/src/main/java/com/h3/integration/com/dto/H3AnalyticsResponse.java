package com.h3.integration.com.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record H3AnalyticsResponse(
        UUID id,
        UUID sessionId,
        String h3Index,
        Double latitude,
        Double longitude,
        Integer avgLatencyMs,
        Double packetLossPercent,
        Integer disconnectCount,
        Double qualityScore,
        Boolean fallbackRecommended,
        ZonedDateTime createdAt) {
}
