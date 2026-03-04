package com.raitu.mitra.dto;

import com.raitu.mitra.model.Listing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

class LivestockPriceDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LivestockPriceResponse {
        private Long id;
        private Listing.AnimalCategory category;
        private String animalName;
        private String animalNameTelugu;
        private String breed;
        private Double pricePerUnit;
        private String unit;
        private Double previousPrice;
        private Double priceChange;
        private Double priceChangePercent;
        private String trend;        // "UP", "DOWN", "STABLE"
        private String market;
        private String district;
        private Boolean isHot;
        private LocalDate priceDate;
    }
}
