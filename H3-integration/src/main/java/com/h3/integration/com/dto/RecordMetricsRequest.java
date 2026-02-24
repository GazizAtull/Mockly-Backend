package com.h3.integration.com.dto;

import java.util.UUID;

public record RecordMetricsRequest(
        UUID sessionId,
        Double latitude,
        Double longitude,
        Integer avgLatencyMs,
        Double packetLossPercent,
        Integer disconnectCount) {
}
