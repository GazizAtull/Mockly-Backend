package com.h3.integration.com.repository;

import com.h3.integration.com.dto.H3ZoneSummaryProjection;
import com.h3.integration.com.entity.H3Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface H3AnalyticsRepository extends JpaRepository<H3Analytics, UUID> {

    @Query("""
           SELECT h.h3Index AS h3Index, 
                  COUNT(h) AS activeSessions, 
                  AVG(h.qualityScore) AS averageQuality, 
                  AVG(h.avgLatencyMs) AS averageLatency, 
                  AVG(h.packetLossPercent) AS averagePacketLoss, 
                  CAST(SUM(h.disconnectCount) AS int) AS totalDisconnects 
           FROM H3Analytics h 
           GROUP BY h.h3Index
           """)
    List<H3ZoneSummaryProjection> getAggregatedZoneSummaries();
}
