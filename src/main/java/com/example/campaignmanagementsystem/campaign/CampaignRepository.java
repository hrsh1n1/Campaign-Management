package com.example.campaignmanagementsystem.campaign;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    Page<Campaign> findBySellerId(UUID sellerId, Pageable pageable);

    @Query("SELECT new com.example.campaignmanagementsystem.campaign.CampaignMetricsSummary(" +
           "COALESCE(SUM(c.impressions), 0L), " +
           "COALESCE(SUM(c.clicks), 0L), " +
           "COALESCE(SUM(c.conversions), 0L)) " +
           "FROM Campaign c")
    CampaignMetricsSummary getMetricsSummary();
}