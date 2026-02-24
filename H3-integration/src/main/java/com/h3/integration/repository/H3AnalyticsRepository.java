package com.h3.integration.repository;

import com.h3.integration.entity.H3Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface H3AnalyticsRepository extends JpaRepository<H3Analytics, UUID> {

    @Query("SELECT DISTINCT h.h3Index FROM H3Analytics h")
    List<String> findDistinctH3Indexes();

    List<H3Analytics> findByH3Index(String h3Index);
}
