package com.raitu.mitra.controller;

import com.raitu.mitra.model.Listing;
import com.raitu.mitra.model.User;
import com.raitu.mitra.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Home screen data API")
public class DashboardController {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final LivestockPriceRepository livestockPriceRepository;
    private final CropPriceRepository cropPriceRepository;
    private final WeatherRepository weatherRepository;

    /**
     * Single API call to populate the entire home screen.
     * Returns: weather, livestock prices, crop prices, nearby listings, user stats.
     */
    @GetMapping
    @Operation(
        summary = "Get all home screen data in one call",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> getDashboard(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng
    ) {
        User user = userRepository.findByPhone(userDetails.getUsername()).orElseThrow();

        Map<String, Object> dashboard = new HashMap<>();

        // ── 1. User Stats ─────────────────────────────────────────────────────
        List<Listing> myListings = listingRepository.findBySeller_Id(user.getId());
        long activeCount = myListings.stream()
            .filter(l -> l.getStatus() == Listing.ListingStatus.ACTIVE).count();

        dashboard.put("userStats", Map.of(
            "name", user.getName(),
            "village", user.getVillage() != null ? user.getVillage() : "",
            "district", user.getDistrict() != null ? user.getDistrict() : "",
            "rating", user.getRating(),
            "totalReviews", user.getTotalReviews(),
            "activeListings", activeCount
        ));

        // ── 2. Weather ────────────────────────────────────────────────────────
        if (user.getDistrict() != null) {
            weatherRepository
                .findFirstByDistrictOrderByRecordedAtDesc(user.getDistrict())
                .ifPresent(w -> dashboard.put("weather", w));
        }

        // ── 3. Livestock Prices (Today) ───────────────────────────────────────
        dashboard.put("livestockPrices",
            livestockPriceRepository.findByPriceDateOrderByCategory(LocalDate.now()));

        // ── 4. Crop Prices (Today, by district) ───────────────────────────────
        if (user.getDistrict() != null) {
            dashboard.put("cropPrices",
                cropPriceRepository.findByDistrictAndPriceDate(user.getDistrict(), LocalDate.now()));
        } else {
            dashboard.put("cropPrices",
                cropPriceRepository.findByPriceDateOrderByCropName(LocalDate.now()));
        }

        // ── 5. Nearby Listings ────────────────────────────────────────────────
        if (lat != null && lng != null) {
            // Location-based nearby
            List<Listing> nearby = listingRepository.findNearbyListings(lat, lng, 30.0);
            dashboard.put("nearbyListings", nearby.subList(0, Math.min(nearby.size(), 5)));
        } else if (user.getDistrict() != null) {
            // District-based fallback
            var pageable = org.springframework.data.domain.PageRequest.of(0, 5,
                org.springframework.data.domain.Sort.by("createdAt").descending());
            dashboard.put("nearbyListings",
                listingRepository
                    .findByDistrictAndStatus(user.getDistrict(), Listing.ListingStatus.ACTIVE, pageable)
                    .getContent());
        }

        return ResponseEntity.ok(Map.of("success", true, "data", dashboard));
    }
}
