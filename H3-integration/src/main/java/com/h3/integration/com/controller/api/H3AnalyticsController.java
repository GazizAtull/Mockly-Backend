package com.h3.integration.com.controller.api;

import com.h3.integration.com.dto.H3AnalyticsResponse;
import com.h3.integration.com.dto.H3ZoneSummary;
import com.h3.integration.com.dto.RecordMetricsRequest;
import com.h3.integration.com.service.H3AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/h3")
@RequiredArgsConstructor
public class H3AnalyticsController {

    private final H3AnalyticsService h3AnalyticsService;

    @PostMapping("/metrics")
    public ResponseEntity<H3AnalyticsResponse> recordMetrics(@RequestBody RecordMetricsRequest request) {
        return ResponseEntity.ok(h3AnalyticsService.recordMetrics(request));
    }

    @GetMapping("/zones")
    public ResponseEntity<List<H3ZoneSummary>> getZoneAnalytics() {
        return ResponseEntity.ok(h3AnalyticsService.getAnalyticsByZone());
    }

    @GetMapping("/zones/issues")
    public ResponseEntity<List<H3ZoneSummary>> getZonesWithIssues() {
        return ResponseEntity.ok(h3AnalyticsService.getZonesWithIssues());
    }
}
