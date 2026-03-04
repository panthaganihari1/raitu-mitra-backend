package com.raitu.mitra.controller;

import com.raitu.mitra.model.Listing;
import com.raitu.mitra.model.User;
import com.raitu.mitra.repository.ListingRepository;
import com.raitu.mitra.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/listings")
@RequiredArgsConstructor
@Tag(name = "Listings", description = "Buy & Sell animal listings")
public class ListingController {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    // ── GET ALL ACTIVE LISTINGS ───────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all active listings (paginated)")
    public ResponseEntity<?> getAllListings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Listing.ListingType type,
        @RequestParam(required = false) Listing.AnimalCategory category,
        @RequestParam(required = false) String district,
        @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Listing> result;

        if (keyword != null && !keyword.isBlank()) {
            result = listingRepository.searchByKeyword(keyword, pageable);
        } else if (district != null && category != null) {
            result = listingRepository.findByDistrictAndCategoryAndStatus(
                district, category, Listing.ListingStatus.ACTIVE, pageable);
        } else if (district != null) {
            result = listingRepository.findByDistrictAndStatus(
                district, Listing.ListingStatus.ACTIVE, pageable);
        } else if (type != null) {
            result = listingRepository.findByTypeAndStatus(
                type, Listing.ListingStatus.ACTIVE, pageable);
        } else if (category != null) {
            result = listingRepository.findByCategoryAndStatus(
                category, Listing.ListingStatus.ACTIVE, pageable);
        } else {
            result = listingRepository.findByStatus(Listing.ListingStatus.ACTIVE, pageable);
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", result.getContent(),
            "totalElements", result.getTotalElements(),
            "totalPages", result.getTotalPages(),
            "currentPage", page
        ));
    }

    // ── GET SINGLE LISTING ────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get listing by ID")
    public ResponseEntity<?> getListingById(@PathVariable Long id) {
        return listingRepository.findById(id)
            .map(listing -> {
                // Increment view count
                listing.setViewCount(listing.getViewCount() + 1);
                listingRepository.save(listing);
                return ResponseEntity.ok(Map.of("success", true, "data", listing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // ── NEARBY LISTINGS ───────────────────────────────────────────────────────

    @GetMapping("/nearby")
    @Operation(summary = "Get listings near a location")
    public ResponseEntity<?> getNearbyListings(
        @RequestParam Double lat,
        @RequestParam Double lng,
        @RequestParam(defaultValue = "20") Double radiusKm
    ) {
        List<Listing> listings = listingRepository.findNearbyListings(lat, lng, radiusKm);
        return ResponseEntity.ok(Map.of("success", true, "data", listings, "count", listings.size()));
    }

    // ── CREATE LISTING ────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new buy/sell listing", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createListing(
        @Valid @RequestBody ListingRequest req,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        User seller = userRepository.findByPhone(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Listing listing = Listing.builder()
            .title(req.title())
            .description(req.description())
            .type(req.type())
            .category(req.category())
            .animalName(req.animalName())
            .quantity(req.quantity())
            .price(req.price())
            .isNegotiable(req.isNegotiable() != null ? req.isNegotiable() : true)
            .age(req.age())
            .weight(req.weight())
            .breed(req.breed())
            .healthStatus(req.healthStatus())
            .village(req.village() != null ? req.village() : seller.getVillage())
            .district(req.district() != null ? req.district() : seller.getDistrict())
            .state(req.state() != null ? req.state() : seller.getState())
            .latitude(req.latitude())
            .longitude(req.longitude())
            .images(req.images())
            .status(Listing.ListingStatus.ACTIVE)
            .viewCount(0)
            .seller(seller)
            .build();

        listing = listingRepository.save(listing);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "లిస్టింగ్ విజయవంతంగా జోడించబడింది! (Listing created successfully!)",
            "data", listing
        ));
    }

    // ── UPDATE LISTING ────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update a listing", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateListing(
        @PathVariable Long id,
        @Valid @RequestBody ListingRequest req,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getPhone().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "success", false, "message", "Not authorized to update this listing"));
        }

        listing.setTitle(req.title());
        listing.setDescription(req.description());
        listing.setPrice(req.price());
        listing.setQuantity(req.quantity());
        listing.setHealthStatus(req.healthStatus());
        if (req.images() != null) listing.setImages(req.images());

        listingRepository.save(listing);
        return ResponseEntity.ok(Map.of("success", true, "message", "Updated successfully", "data", listing));
    }

    // ── MARK AS SOLD ──────────────────────────────────────────────────────────

    @PatchMapping("/{id}/sold")
    @Operation(summary = "Mark listing as sold", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> markAsSold(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getPhone().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "success", false, "message", "Not authorized"));
        }

        listing.setStatus(Listing.ListingStatus.SOLD);
        listingRepository.save(listing);
        return ResponseEntity.ok(Map.of("success", true, "message", "అమ్మకం విజయవంతమైంది! (Marked as Sold!)"));
    }

    // ── DELETE LISTING ────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a listing", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteListing(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getPhone().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "success", false, "message", "Not authorized"));
        }

        listing.setStatus(Listing.ListingStatus.DELETED);
        listingRepository.save(listing);
        return ResponseEntity.ok(Map.of("success", true, "message", "Listing deleted"));
    }

    // ── MY LISTINGS ───────────────────────────────────────────────────────────

    @GetMapping("/my")
    @Operation(summary = "Get current user's listings", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getMyListings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByPhone(userDetails.getUsername()).orElseThrow();
        List<Listing> listings = listingRepository.findBySeller_Id(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "data", listings, "count", listings.size()));
    }

    // ── RECORD (inner) ────────────────────────────────────────────────────────

    public record ListingRequest(
        String title, String description,
        Listing.ListingType type,
        Listing.AnimalCategory category,
        String animalName, Integer quantity,
        Double price, Boolean isNegotiable,
        String age, String weight, String breed,
        String healthStatus, String village,
        String district, String state,
        Double latitude, Double longitude,
        List<String> images
    ) {}
}
