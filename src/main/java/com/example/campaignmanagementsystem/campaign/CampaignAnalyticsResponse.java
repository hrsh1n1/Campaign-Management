package com.example.campaignmanagementsystem.campaign;

public record CampaignAnalyticsResponse(
        long totalImpressions,
        long totalClicks,
        long totalConversions,
        double clickThroughRate,
        double conversionRate
) {
}
