package com.raitu.mitra.dto;

import com.raitu.mitra.model.Listing;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

class ListingDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingRequest {
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private Listing.ListingType type;
        @NotNull
        private Listing.AnimalCategory category;
        @NotBlank
        private String animalName;
        @Min(1)
        private Integer quantity;
        @Positive
        private Double price;
        private Boolean isNegotiable;
        private String age;
        private String weight;
        private String breed;
        private String healthStatus;
        private String village;
        private String district;
        private String state;
        private Double latitude;
        private Double longitude;
        private List<String> images;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingResponse {
        private Long id;
        private String title;
        private String description;
        private Listing.ListingType type;
        private Listing.AnimalCategory category;
        private String animalName;
        private Integer quantity;
        private Double price;
        private Boolean isNegotiable;
        private String age;
        private String weight;
        private String breed;
        private String healthStatus;
        private String village;
        private String district;
        private String state;
        private Double latitude;
        private Double longitude;
        private List<String> images;
        private Listing.ListingStatus status;
        private Integer viewCount;
        private SellerSummary seller;
        private LocalDateTime createdAt;
        private Double distanceKm;  // populated for nearby searches
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerSummary {
        private Long id;
        private String name;
        private String phone;
        private String village;
        private Double rating;
        private String profileImage;
    }
}
