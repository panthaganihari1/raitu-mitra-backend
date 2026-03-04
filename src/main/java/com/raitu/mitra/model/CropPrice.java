package com.raitu.mitra.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crop_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cropName;           // "Red Chilli"

    private String cropNameTelugu;     // "ఎర్ర మిరపకాయ"
    private String emoji;              // 🌶️
    private String grade;              // "Grade A", "Grade B"

    @Column(nullable = false)
    private Double pricePerKg;

    private Double minPrice;
    private Double maxPrice;
    private Double modalPrice;         // Most common price

    private Double previousPrice;
    private Double priceChange;
    private Double priceChangePercent;

    @Column(nullable = false)
    private String market;             // "Guntur Market"

    private String district;
    private String state;

    private LocalDate priceDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (priceDate == null) priceDate = LocalDate.now();
        if (previousPrice != null && previousPrice > 0) {
            priceChange = pricePerKg - previousPrice;
            priceChangePercent = (priceChange / previousPrice) * 100;
        }
    }
}
