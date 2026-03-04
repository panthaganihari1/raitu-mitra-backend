package com.raitu.mitra.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String state;

    private Double latitude;
    private Double longitude;

    // Current conditions
    private Double temperature;
    private Double feelsLike;
    private Double humidity;
    private Double windSpeed;
    private String windDirection;
    private Double rainProbability;
    private Double rainfall;           // mm

    @Column(name = "`condition`")      // <-- ESCAPE RESERVED WORD
    private String condition;          // "Partly Cloudy", "Sunny"

    @Column(name = "condition_telugu")
    private String conditionTelugu;    // "మేఘావృతం"

    private String icon;               // "🌤️"

    // Advisory for farmers
    @Column(columnDefinition = "TEXT")
    private String farmingAdvisory;

    @Column(columnDefinition = "TEXT")
    private String farmingAdvisoryTelugu;

    private LocalDateTime recordedAt;
    private LocalDateTime nextUpdateAt;

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) recordedAt = LocalDateTime.now();
    }
}