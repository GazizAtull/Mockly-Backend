package com.h3.integration.com.dto;

public record H3ZoneSummary(
        String h3Index,
        int activeSessions,
        double averageQuality,
        double averageLatency,
        double averagePacketLoss,
        int totalDisconnects,
        boolean fallbackRecommended) {
}
