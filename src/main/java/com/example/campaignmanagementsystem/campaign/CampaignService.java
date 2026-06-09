package com.example.campaignmanagementsystem.campaign;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CampaignService {

    CampaignResponse createCampaign(CreateCampaignRequest request);

    CampaignResponse updateCampaign(UpdateCampaignRequest request);

    void deleteCampaign(UUID campaignId);

    CampaignResponse getCampaignById(UUID campaignId);

    Page<CampaignResponse> getAllCampaigns(Pageable pageable);

    Page<CampaignResponse> getCampaignsBySellerId(UUID sellerId, Pageable pageable);

    CampaignResponse updateCampaignMetrics(UUID campaignId, UpdateCampaignMetricsRequest request);

    CampaignResponse incrementImpressions(UUID campaignId);

    CampaignResponse incrementClicks(UUID campaignId);

    CampaignResponse incrementConversions(UUID campaignId);

    CampaignAnalyticsResponse getSystemAnalytics();
}
