package com.h3.integration.com.dto;

public interface H3ZoneSummaryProjection {
    String getH3Index();
    int getActiveSessions();
    double getAverageQuality();
    double getAverageLatency();
    double getAveragePacketLoss();
    int getTotalDisconnects();
}
