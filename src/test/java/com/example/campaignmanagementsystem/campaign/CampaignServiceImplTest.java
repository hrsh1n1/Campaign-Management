package com.example.campaignmanagementsystem.campaign;

import com.example.campaignmanagementsystem.account.Account;
import com.example.campaignmanagementsystem.account.AccountService;
import com.example.campaignmanagementsystem.keyword.Keyword;
import com.example.campaignmanagementsystem.keyword.KeywordDTO;
import com.example.campaignmanagementsystem.keyword.KeywordMapper;
import com.example.campaignmanagementsystem.keyword.KeywordService;
import com.example.campaignmanagementsystem.location.Location;
import com.example.campaignmanagementsystem.location.LocationDTO;
import com.example.campaignmanagementsystem.location.LocationMapper;
import com.example.campaignmanagementsystem.location.LocationService;
import com.example.campaignmanagementsystem.product.Product;
import com.example.campaignmanagementsystem.product.ProductDTO;
import com.example.campaignmanagementsystem.product.ProductMapper;
import com.example.campaignmanagementsystem.product.ProductService;
import com.example.campaignmanagementsystem.seller.Seller;
import com.example.campaignmanagementsystem.seller.SellerDTO;
import com.example.campaignmanagementsystem.seller.SellerMapper;
import com.example.campaignmanagementsystem.seller.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private KeywordService keywordService;

    @Mock
    private AccountService accountService;

    @Mock
    private ProductService productService;

    @Mock
    private SellerService sellerService;

    @Mock
    private LocationService locationService;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private SellerMapper sellerMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private KeywordMapper keywordMapper;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testGetAllCampaigns_WithPagination() {

        Location location1 = Location.builder()
                .id(UUID.randomUUID())
                .town("Warsaw")
                .build();

        Location location2 = Location.builder()
                .id(UUID.randomUUID())
                .town("Krakow")
                .build();

        Seller seller = Seller.builder()
                .id(UUID.randomUUID())
                .name("Seller")
                .build();

        Campaign campaign1 = Campaign.builder()
                .id(UUID.randomUUID())
                .name("Campaign 1")
                .bidAmount(BigDecimal.valueOf(1.0))
                .campaignFund(BigDecimal.valueOf(100.0))
                .status(CampaignStatus.ON)
                .radius(10)
                .location(location1)
                .keywords(new HashSet<>())
                .product(new Product())
                .seller(seller)
                .build();

        Campaign campaign2 = Campaign.builder()
                .id(UUID.randomUUID())
                .name("Campaign 2")
                .bidAmount(BigDecimal.valueOf(2.0))
                .campaignFund(BigDecimal.valueOf(200.0))
                .status(CampaignStatus.OFF)
                .radius(20)
                .location(location2)
                .keywords(new HashSet<>())
                .product(new Product())
                .seller(seller)
                .build();

        List<Campaign> campaigns = Arrays.asList(campaign1, campaign2);
        Page<Campaign> campaignPage = new PageImpl<>(campaigns);

        when(campaignMapper.toResponse(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign campaign = invocation.getArgument(0);
            return new CampaignResponse(
                    campaign.getId(),
                    campaign.getName(),
                    campaign.getBidAmount(),
                    campaign.getCampaignFund(),
                    campaign.getStatus(),
                    campaign.getRadius(),
                    campaign.getLocation().getTown(),
                    new ArrayList<>(),
                    campaign.getProduct().getId(),
                    campaign.getSeller().getId(),
                    campaign.getImpressions(),
                    campaign.getClicks(),
                    campaign.getConversions()
            );
        });
        when(campaignRepository.findAll(any(Pageable.class))).thenReturn(campaignPage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CampaignResponse> result = campaignService.getAllCampaigns(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertNotNull(result.getContent().get(0));
        assertNotNull(result.getContent().get(1));
        assertEquals("Campaign 1", result.getContent().get(0).name());
        assertEquals("Campaign 2", result.getContent().get(1).name());

        verify(campaignRepository, times(1)).findAll(pageable);
    }


    @Test
    public void testCreateCampaign_Success() {
        UUID sellerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateCampaignRequest request = new CreateCampaignRequest(
                "Test Campaign",
                BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(100.0),
                CampaignStatus.ON,
                10,
                "Warsaw",
                Collections.singletonList("keyword1"),
                productId,
                sellerId
        );

        SellerDTO seller = new SellerDTO(sellerId, "Seller");
        ProductDTO product = new ProductDTO(productId, "Product", "Description", BigDecimal.valueOf(10.0));
        KeywordDTO keyword = new KeywordDTO(UUID.randomUUID(), "keyword1");
        LocationDTO location = new LocationDTO(UUID.randomUUID(), "Warsaw");

        when(sellerService.getSellerById(sellerId)).thenReturn(seller);
        when(productService.getProductById(productId)).thenReturn(product);
        when(accountService.hasSufficientFunds(sellerId, request.campaignFund())).thenReturn(true);
        doNothing().when(accountService).withdraw(sellerId, request.campaignFund());
        when(keywordService.findOrCreateByValue("keyword1")).thenReturn(keyword);
        when(locationService.getLocationByTown("Warsaw")).thenReturn(location);

        when(sellerMapper.toEntity(seller)).thenReturn(
                Seller.builder()
                        .id(sellerId)
                        .name("Seller")
                        .account(new Account())
                        .build()
        );
        when(productMapper.toEntity(product)).thenReturn(Product.builder()
                .id(productId)
                .name("Product")
                .description("Description")
                .price(BigDecimal.valueOf(10.0))
                .build());
        when(locationMapper.toEntity(location)).thenReturn(new Location(UUID.randomUUID(), "Warsaw"));
        when(keywordMapper.toEntity(keyword)).thenReturn(new Keyword());

        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign campaign = invocation.getArgument(0);
            campaign.setId(UUID.randomUUID());
            return campaign;
        });

        when(campaignMapper.toResponse(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign campaign = invocation.getArgument(0);
            return new CampaignResponse(
                    campaign.getId(),
                    campaign.getName(),
                    campaign.getBidAmount(),
                    campaign.getCampaignFund(),
                    campaign.getStatus(),
                    campaign.getRadius(),
                    campaign.getLocation().getTown(),
                    campaign.getKeywords().stream()
                            .map(Keyword::getText)
                            .collect(Collectors.toList()),
                    campaign.getProduct().getId(),
                    campaign.getSeller().getId(),
                    campaign.getImpressions(),
                    campaign.getClicks(),
                    campaign.getConversions()
            );
        });

        CampaignResponse response = campaignService.createCampaign(request);

        assertNotNull(response);
        assertEquals(request.name(), response.name());
        assertEquals(request.bidAmount(), response.bidAmount());
        assertEquals(request.campaignFund(), response.campaignFund());
        assertEquals(request.status(), response.status());
        assertEquals(request.radius(), response.radius());
        assertEquals(request.town(), response.town());
        assertEquals(request.productId(), response.productId());
        assertEquals(request.sellerId(), response.sellerId());

        verify(accountService, times(1)).hasSufficientFunds(sellerId, request.campaignFund());
        verify(accountService, times(1)).withdraw(sellerId, request.campaignFund());
        verify(campaignRepository, times(1)).save(any(Campaign.class));
    }

    @Test
    public void testUpdateCampaignMetrics_Success() {
        UUID campaignId = UUID.randomUUID();
        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .impressions(10)
                .clicks(5)
                .conversions(1)
                .build();

        UpdateCampaignMetricsRequest request = new UpdateCampaignMetricsRequest(100, 50, 10);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(campaignMapper.toResponse(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign c = invocation.getArgument(0);
            return new CampaignResponse(
                    c.getId(), c.getName(), c.getBidAmount(), c.getCampaignFund(), c.getStatus(),
                    c.getRadius(), "Warsaw", new ArrayList<>(), UUID.randomUUID(), UUID.randomUUID(),
                    c.getImpressions(), c.getClicks(), c.getConversions()
            );
        });

        CampaignResponse response = campaignService.updateCampaignMetrics(campaignId, request);

        assertNotNull(response);
        assertEquals(100, response.impressions());
        assertEquals(50, response.clicks());
        assertEquals(10, response.conversions());
        verify(campaignRepository, times(1)).findById(campaignId);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testIncrementImpressions_Success() {
        UUID campaignId = UUID.randomUUID();
        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .impressions(10)
                .clicks(5)
                .conversions(1)
                .build();

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(campaignMapper.toResponse(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign c = invocation.getArgument(0);
            return new CampaignResponse(
                    c.getId(), c.getName(), c.getBidAmount(), c.getCampaignFund(), c.getStatus(),
                    c.getRadius(), "Warsaw", new ArrayList<>(), UUID.randomUUID(), UUID.randomUUID(),
                    c.getImpressions(), c.getClicks(), c.getConversions()
            );
        });

        CampaignResponse response = campaignService.incrementImpressions(campaignId);

        assertNotNull(response);
        assertEquals(11, response.impressions());
        verify(campaignRepository, times(1)).findById(campaignId);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testIncrementClicks_Success() {
        UUID campaignId = UUID.randomUUID();
        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .impressions(10)
                .clicks(5)
                .conversions(1)
                .build();

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(campaignMapper.toResponse(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign c = invocation.getArgument(0);
            return new CampaignResponse(
                    c.getId(), c.getName(), c.getBidAmount(), c.getCampaignFund(), c.getStatus(),
                    c.getRadius(), "Warsaw", new ArrayList<>(), UUID.randomUUID(), UUID.randomUUID(),
                    c.getImpressions(), c.getClicks(), c.getConversions()
            );
        });

        CampaignResponse response = campaignService.incrementClicks(campaignId);

        assertNotNull(response);
        assertEquals(6, response.clicks());
        verify(campaignRepository, times(1)).findById(campaignId);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testIncrementConversions_Success() {
        UUID campaignId = UUID.randomUUID();
        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .impressions(10)
                .clicks(5)
                .conversions(1)
                .build();

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(campaignMapper.toResponse(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign c = invocation.getArgument(0);
            return new CampaignResponse(
                    c.getId(), c.getName(), c.getBidAmount(), c.getCampaignFund(), c.getStatus(),
                    c.getRadius(), "Warsaw", new ArrayList<>(), UUID.randomUUID(), UUID.randomUUID(),
                    c.getImpressions(), c.getClicks(), c.getConversions()
            );
        });

        CampaignResponse response = campaignService.incrementConversions(campaignId);

        assertNotNull(response);
        assertEquals(2, response.conversions());
        verify(campaignRepository, times(1)).findById(campaignId);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testGetSystemAnalytics_Success() {
        CampaignMetricsSummary summary = new CampaignMetricsSummary(1000L, 50L, 5L);
        when(campaignRepository.getMetricsSummary()).thenReturn(summary);

        CampaignAnalyticsResponse analytics = campaignService.getSystemAnalytics();

        assertNotNull(analytics);
        assertEquals(1000L, analytics.totalImpressions());
        assertEquals(50L, analytics.totalClicks());
        assertEquals(5L, analytics.totalConversions());
        assertEquals(5.0, analytics.clickThroughRate()); // (50 / 1000) * 100
        assertEquals(10.0, analytics.conversionRate()); // (5 / 50) * 100
        verify(campaignRepository, times(1)).getMetricsSummary();
    }

    @Test
    public void testGetSystemAnalytics_DivisionByZero() {
        CampaignMetricsSummary summary = new CampaignMetricsSummary(0L, 0L, 0L);
        when(campaignRepository.getMetricsSummary()).thenReturn(summary);

        CampaignAnalyticsResponse analytics = campaignService.getSystemAnalytics();

        assertNotNull(analytics);
        assertEquals(0L, analytics.totalImpressions());
        assertEquals(0L, analytics.totalClicks());
        assertEquals(0L, analytics.totalConversions());
        assertEquals(0.0, analytics.clickThroughRate());
        assertEquals(0.0, analytics.conversionRate());
        verify(campaignRepository, times(1)).getMetricsSummary();
    }
}
