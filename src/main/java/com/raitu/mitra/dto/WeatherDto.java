package com.raitu.mitra.dto;

import lombok.*;

import java.time.LocalDateTime;

// ─── AUTH ────────────────────────────────────────────────────────────────────

// ─── LISTING ─────────────────────────────────────────────────────────────────

// ─── LIVESTOCK PRICE ──────────────────────────────────────────────────────────

// ─── CROP PRICE ───────────────────────────────────────────────────────────────

// ─── WEATHER ─────────────────────────────────────────────────────────────────

class WeatherDto {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WeatherResponse {
        private String district;
        private String state;
        private Double temperature;
        private Double feelsLike;
        private Double humidity;
        private Double windSpeed;
        private String windDirection;
        private Double rainProbability;
        private Double rainfall;
        private String condition;
        private String conditionTelugu;
        private String icon;
        private String farmingAdvisory;
        private String farmingAdvisoryTelugu;
        private LocalDateTime recordedAt;
    }
}

// ─── DASHBOARD ───────────────────────────────────────────────────────────────

// ─── API RESPONSE WRAPPER ────────────────────────────────────────────────────

