package com.example.campaignmanagementsystem.api.V1;

import com.example.campaignmanagementsystem.campaign.CampaignResponse;
import com.example.campaignmanagementsystem.campaign.CreateCampaignRequest;
import com.example.campaignmanagementsystem.campaign.UpdateCampaignRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.campaignmanagementsystem.campaign.CampaignAnalyticsResponse;
import com.example.campaignmanagementsystem.campaign.UpdateCampaignMetricsRequest;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/v1/campaigns")
@Tag(name = "Campaign", description = "Operations related to campaigns")
public interface CampaignController {

    @Operation(summary = "Create a new campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Campaign created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    ResponseEntity<CampaignResponse> createCampaign(@RequestBody CreateCampaignRequest request);

    @Operation(summary = "Update an existing campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign updated"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    @PutMapping("/{campaignId}")
    ResponseEntity<CampaignResponse> updateCampaign(@PathVariable UUID campaignId, @RequestBody UpdateCampaignRequest request);

    @Operation(summary = "Delete a campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Campaign deleted"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    @DeleteMapping("/{campaignId}")
    ResponseEntity<Void> deleteCampaign(@PathVariable UUID campaignId);

    @Operation(summary = "Get campaign by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign retrieved"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    @GetMapping("/{campaignId}")
    ResponseEntity<CampaignResponse> getCampaignById(@PathVariable UUID campaignId);

    @Operation(summary = "Get all campaigns")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of campaigns retrieved")
    })
    @GetMapping
    ResponseEntity<Page<CampaignResponse>> getAllCampaigns(Pageable pageable);

    @Operation(summary = "Get campaigns by seller ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of seller's campaigns retrieved")
    })
    @GetMapping("/seller/{sellerId}")
    ResponseEntity<Page<CampaignResponse>> getCampaignsBySellerId(@PathVariable UUID sellerId, Pageable pageable);

    @Operation(summary = "Update campaign performance metrics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics updated"),
            @ApiResponse(responseCode = "404", description = "Campaign not found"),
            @ApiResponse(responseCode = "400", description = "Invalid metrics data")
    })
    @PutMapping("/{campaignId}/metrics")
    ResponseEntity<CampaignResponse> updateCampaignMetrics(
            @PathVariable UUID campaignId,
            @Valid @RequestBody UpdateCampaignMetricsRequest request
    );

    @Operation(summary = "Increment campaign impressions by 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Impressions incremented"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    @PostMapping("/{campaignId}/track-impression")
    ResponseEntity<CampaignResponse> incrementImpressions(@PathVariable UUID campaignId);

    @Operation(summary = "Increment campaign clicks by 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clicks incremented"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    @PostMapping("/{campaignId}/track-click")
    ResponseEntity<CampaignResponse> incrementClicks(@PathVariable UUID campaignId);

    @Operation(summary = "Increment campaign conversions by 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversions incremented"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    @PostMapping("/{campaignId}/track-conversion")
    ResponseEntity<CampaignResponse> incrementConversions(@PathVariable UUID campaignId);

    @Operation(summary = "Get system-wide campaign performance analytics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics retrieved")
    })
    @GetMapping("/analytics")
    ResponseEntity<CampaignAnalyticsResponse> getSystemAnalytics();
}
