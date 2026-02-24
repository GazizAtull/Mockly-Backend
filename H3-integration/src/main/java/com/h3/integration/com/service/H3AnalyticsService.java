package com.h3.integration.com.service;

import com.h3.integration.com.dto.H3AnalyticsResponse;
import com.h3.integration.com.dto.H3ZoneSummary;
import com.h3.integration.com.dto.RecordMetricsRequest;
import com.h3.integration.com.entity.H3Analytics;
import com.h3.integration.com.repository.H3AnalyticsRepository;
import com.uber.h3core.H3Core;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class H3AnalyticsService {

    private final H3AnalyticsRepository h3AnalyticsRepository;
    private final H3AuditPublisher auditPublisher;
    private final H3Core h3Core;

    private static final int H3_RESOLUTION = 7;
    private static final double QUALITY_THRESHOLD = 50.0;

    @Transactional
    public H3AnalyticsResponse recordMetrics(RecordMetricsRequest request) {
        String h3Index = h3Core.latLngToCellAddress(
                request.latitude(), request.longitude(), H3_RESOLUTION);

        double qualityScore = calculateQualityScore(
                request.avgLatencyMs(), request.packetLossPercent(),
                request.disconnectCount() != null ? request.disconnectCount() : 0);

        boolean fallbackRecommended = qualityScore < QUALITY_THRESHOLD;

        H3Analytics analytics = H3Analytics.builder()
                .sessionId(request.sessionId())
                .h3Index(h3Index)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .avgLatencyMs(request.avgLatencyMs())
                .packetLossPercent(request.packetLossPercent())
                .disconnectCount(request.disconnectCount() != null ? request.disconnectCount() : 0)
                .qualityScore(qualityScore)
                .fallbackRecommended(fallbackRecommended)
                .build();

        analytics = h3AnalyticsRepository.save(analytics);
        log.info("Recorded H3 analytics: h3={} quality={} fallback={}", h3Index, qualityScore, fallbackRecommended);

        auditPublisher.publishEvent(null, "H3_ANALYTICS_RECORDED", "SESSION", request.sessionId(), "SUCCESS",
                String.format("Metrics recorded for zone %s with score %.2f", h3Index, qualityScore));

        return toResponse(analytics);
    }

    @Transactional(readOnly = true)
    public List<H3ZoneSummary> getAnalyticsByZone() {
        return h3AnalyticsRepository.getAggregatedZoneSummaries().stream()
                .map(proj -> new H3ZoneSummary(
                        proj.getH3Index(),
                        proj.getActiveSessions(),
                        proj.getAverageQuality(),
                        proj.getAverageLatency(),
                        proj.getAveragePacketLoss(),
                        proj.getTotalDisconnects(),
                        proj.getAverageQuality() < QUALITY_THRESHOLD // Вычисляем fallback на лету
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<H3ZoneSummary> getZonesWithIssues() {
        return getAnalyticsByZone().stream()
                .filter(H3ZoneSummary::fallbackRecommended)
                .toList();
    }

    private double calculateQualityScore(int latencyMs, double packetLossPercent, int disconnects) {
        double latencyScore = Math.max(0, 100 - (latencyMs / 5.0));
        double packetLossScore = Math.max(0, 100 - (packetLossPercent * 10));
        double disconnectScore = Math.max(0, 100 - (disconnects * 20.0));
        return (latencyScore * 0.4) + (packetLossScore * 0.4) + (disconnectScore * 0.2);
    }

    private H3ZoneSummary aggregateZone(String h3Index, List<H3Analytics> records) {
        double avgQuality = records.stream()
                .mapToDouble(H3Analytics::getQualityScore)
                .average().orElse(0.0);

        double avgLatency = records.stream()
                .mapToDouble(H3Analytics::getAvgLatencyMs)
                .average().orElse(0.0);

        double avgPacketLoss = records.stream()
                .mapToDouble(H3Analytics::getPacketLossPercent)
                .average().orElse(0.0);

        int totalDisconnects = records.stream()
                .mapToInt(H3Analytics::getDisconnectCount)
                .sum();

        boolean fallback = avgQuality < QUALITY_THRESHOLD;

        return new H3ZoneSummary(
                h3Index, records.size(), avgQuality,
                avgLatency, avgPacketLoss, totalDisconnects, fallback);
    }

    private H3AnalyticsResponse toResponse(H3Analytics entity) {
        return new H3AnalyticsResponse(
                entity.getId(),
                entity.getSessionId(),
                entity.getH3Index(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getAvgLatencyMs(),
                entity.getPacketLossPercent(),
                entity.getDisconnectCount(),
                entity.getQualityScore(),
                entity.getFallbackRecommended(),
                entity.getCreatedAt());
    }
}
