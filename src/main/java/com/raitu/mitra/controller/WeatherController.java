package com.raitu.mitra.controller;

import com.raitu.mitra.model.WeatherData;
import com.raitu.mitra.repository.WeatherRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "Weather data for farming districts")
public class WeatherController {

    private final WeatherRepository weatherRepository;

    @GetMapping("/{district}")
    @Operation(summary = "Get latest weather for a district")
    public ResponseEntity<?> getWeather(@PathVariable String district) {
        return weatherRepository.findFirstByDistrictOrderByRecordedAtDesc(district)
            .map(w -> ResponseEntity.ok(Map.of("success", true, "data", w)))
            .orElse(ResponseEntity.ok(Map.of(
                "success", false,
                "message", "No weather data available for " + district
            )));
    }

    @GetMapping("/{district}/{state}")
    @Operation(summary = "Get weather for a specific district and state")
    public ResponseEntity<?> getWeatherByDistrictAndState(
        @PathVariable String district,
        @PathVariable String state
    ) {
        return weatherRepository.findFirstByDistrictAndStateOrderByRecordedAtDesc(district, state)
            .map(w -> ResponseEntity.ok(Map.of("success", true, "data", w)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add / update weather data (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addWeather(@RequestBody WeatherData weatherData) {
        weatherData.setRecordedAt(LocalDateTime.now());
        WeatherData saved = weatherRepository.save(weatherData);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true, "message", "Weather data saved", "data", saved));
    }

    /**
     * Scheduled task to refresh weather data.
     * In production, integrate with India Meteorological Department (IMD) or
     * OpenWeatherMap API for real data.
     * Runs every 3 hours.
     */
    @Scheduled(fixedDelay = 3 * 60 * 60 * 1000)
    public void refreshWeatherData() {
        // TODO: Call IMD / OpenWeatherMap API and update DB
        // Example districts to refresh: Guntur, Kurnool, Warangal, etc.
        System.out.println("[WeatherScheduler] Refreshing weather data at " + LocalDateTime.now());
    }
}
