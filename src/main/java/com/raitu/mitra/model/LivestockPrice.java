package com.raitu.mitra.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "livestock_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivestockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Listing.AnimalCategory category;

    private String animalName;         // "Country Hen"
    private String animalNameTelugu;   // "నాటు కోడి"
    private String breed;

    @Column(nullable = false)
    private Double pricePerUnit;

    private String unit;               // "each", "per kg"
    private Double previousPrice;
    private Double priceChange;        // +20 or -200
    private Double priceChangePercent;

    private String market;             // "Guntur", "Kurnool"
    private String district;
    private String state;

    private LocalDate priceDate;

    @Column(name = "is_hot")
    private Boolean isHot = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (priceDate == null) priceDate = LocalDate.now();
        // Calculate change
        if (previousPrice != null && previousPrice > 0) {
            priceChange = pricePerUnit - previousPrice;
            priceChangePercent = (priceChange / previousPrice) * 100;
        }
    }
}
