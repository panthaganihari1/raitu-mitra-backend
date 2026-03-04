package com.raitu.mitra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

class DashboardDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardResponse {
        private UserStats userStats;
        private WeatherDto.WeatherResponse weather;
        private List<LivestockPriceDTOs.LivestockPriceResponse> topLivestockPrices;
        private List<CropPriceDTOs.CropPriceResponse> topCropPrices;
        private List<ListingDTOs.ListingResponse> nearbyListings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Double monthlyEarnings;
        private Integer activeListings;
        private Double rating;
    }
}
