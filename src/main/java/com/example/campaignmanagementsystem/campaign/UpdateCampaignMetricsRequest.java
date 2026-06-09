package com.example.campaignmanagementsystem.campaign;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCampaignMetricsRequest(
        @NotNull(message = "Impressions count is mandatory")
        @Min(value = 0, message = "Impressions must be at least 0")
        Integer impressions,

        @NotNull(message = "Clicks count is mandatory")
        @Min(value = 0, message = "Clicks must be at least 0")
        Integer clicks,

        @NotNull(message = "Conversions count is mandatory")
        @Min(value = 0, message = "Conversions must be at least 0")
        Integer conversions
) {
}
