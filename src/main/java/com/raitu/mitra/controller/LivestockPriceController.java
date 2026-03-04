package com.raitu.mitra.controller;

import com.raitu.mitra.model.Listing;
import com.raitu.mitra.model.LivestockPrice;
import com.raitu.mitra.repository.LivestockPriceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/livestock-prices")
@RequiredArgsConstructor
@Tag(name = "Livestock Prices", description = "Daily animal market rates")
public class LivestockPriceController {

    private final LivestockPriceRepository priceRepository;

    @GetMapping("/today")
    @Operation(summary = "Get all livestock prices for today")
    public ResponseEntity<?> getTodayPrices() {
        List<LivestockPrice> prices = priceRepository.findByPriceDateOrderByCategory(LocalDate.now());
        return ResponseEntity.ok(Map.of("success", true, "data", prices, "date", LocalDate.now()));
    }

    @GetMapping("/market/{market}")
    @Operation(summary = "Get prices by market name")
    public ResponseEntity<?> getPricesByMarket(@PathVariable String market) {
        List<LivestockPrice> prices = priceRepository.findByMarketAndPriceDate(market, LocalDate.now());
        return ResponseEntity.ok(Map.of("success", true, "data", prices, "market", market));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get price history for a specific animal category")
    public ResponseEntity<?> getPricesByCategory(@PathVariable Listing.AnimalCategory category) {
        List<LivestockPrice> prices = priceRepository.findByCategoryOrderByPriceDateDesc(category);
        return ResponseEntity.ok(Map.of("success", true, "data", prices));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add new livestock price (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addPrice(@RequestBody LivestockPrice price) {
        LivestockPrice saved = priceRepository.save(price);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true, "message", "Price added successfully", "data", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update livestock price (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updatePrice(@PathVariable Long id, @RequestBody LivestockPrice updatedPrice) {
        return priceRepository.findById(id)
            .map(price -> {
                price.setPricePerUnit(updatedPrice.getPricePerUnit());
                price.setPreviousPrice(price.getPricePerUnit());
                price.setMarket(updatedPrice.getMarket());
                price.setIsHot(updatedPrice.getIsHot());
                priceRepository.save(price);
                return ResponseEntity.ok(Map.of("success", true, "data", price));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
