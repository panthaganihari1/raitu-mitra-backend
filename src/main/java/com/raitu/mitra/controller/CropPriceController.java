package com.raitu.mitra.controller;

import com.raitu.mitra.model.CropPrice;
import com.raitu.mitra.repository.CropPriceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/crop-prices")
@RequiredArgsConstructor
@Tag(name = "Crop Prices", description = "Daily mandi crop rates")
public class CropPriceController {

    private final CropPriceRepository cropPriceRepository;

    @GetMapping("/today")
    @Operation(summary = "Get all crop prices for today")
    public ResponseEntity<?> getTodayPrices() {
        List<CropPrice> prices = cropPriceRepository.findByPriceDateOrderByCropName(LocalDate.now());
        return ResponseEntity.ok(Map.of("success", true, "data", prices, "date", LocalDate.now()));
    }

    @GetMapping("/market/{market}")
    @Operation(summary = "Get crop prices by market (mandi)")
    public ResponseEntity<?> getPricesByMarket(@PathVariable String market) {
        List<CropPrice> prices = cropPriceRepository
            .findByMarketAndPriceDateOrderByCropName(market, LocalDate.now());
        return ResponseEntity.ok(Map.of("success", true, "data", prices, "market", market));
    }

    @GetMapping("/district/{district}")
    @Operation(summary = "Get crop prices by district")
    public ResponseEntity<?> getPricesByDistrict(@PathVariable String district) {
        List<CropPrice> prices = cropPriceRepository
            .findByDistrictAndPriceDate(district, LocalDate.now());
        return ResponseEntity.ok(Map.of("success", true, "data", prices));
    }

    @GetMapping("/crop/{cropName}/history")
    @Operation(summary = "Get price history of a specific crop")
    public ResponseEntity<?> getCropHistory(@PathVariable String cropName) {
        List<CropPrice> prices = cropPriceRepository.findByCropNameOrderByPriceDateDesc(cropName);
        return ResponseEntity.ok(Map.of("success", true, "data", prices, "crop", cropName));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get all crop prices for a specific date")
    public ResponseEntity<?> getPricesByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CropPrice> prices = cropPriceRepository.findByPriceDateOrderByCropName(date);
        return ResponseEntity.ok(Map.of("success", true, "data", prices, "date", date));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add new crop price (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addPrice(@RequestBody CropPrice cropPrice) {
        CropPrice saved = cropPriceRepository.save(cropPrice);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true, "message", "Crop price added", "data", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update crop price (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updatePrice(@PathVariable Long id, @RequestBody CropPrice updated) {
        return cropPriceRepository.findById(id)
            .map(price -> {
                price.setPreviousPrice(price.getPricePerKg());
                price.setPricePerKg(updated.getPricePerKg());
                price.setMinPrice(updated.getMinPrice());
                price.setMaxPrice(updated.getMaxPrice());
                price.setModalPrice(updated.getModalPrice());
                cropPriceRepository.save(price);
                return ResponseEntity.ok(Map.of("success", true, "data", price));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
