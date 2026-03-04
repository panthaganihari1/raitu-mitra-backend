package com.raitu.mitra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

class CropPriceDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CropPriceResponse {
        private Long id;
        private String cropName;
        private String cropNameTelugu;
        private String emoji;
        private String grade;
        private Double pricePerKg;
        private Double minPrice;
        private Double maxPrice;
        private Double modalPrice;
        private Double previousPrice;
        private Double priceChange;
        private Double priceChangePercent;
        private String trend;        // "UP", "DOWN", "STABLE"
        private String market;
        private String district;
        private LocalDate priceDate;
    }
}
