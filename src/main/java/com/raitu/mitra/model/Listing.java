package com.raitu.mitra.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType type;   // BUY or SELL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalCategory category;

    @Column(nullable = false)
    private String animalName;  // e.g. "Country Hen", "Nellore Goat"

    private Integer quantity;
    private Double price;          // per unit price

    @Column(name = "is_negotiable")
    private Boolean isNegotiable = true;

    private String age;            // e.g. "6 months", "2 years"
    private String weight;         // e.g. "35 kg"
    private String breed;
    private String healthStatus;   // "Vaccinated", "Healthy"

    // Location
    private String village;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;

    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "image_url")
    private List<String> images;

    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ListingType {
        BUY, SELL
    }

    public enum AnimalCategory {
        HEN, GOAT, COW, BUFFALO, PIG, SHEEP, DUCK, OTHER
    }

    public enum ListingStatus {
        ACTIVE, SOLD, EXPIRED, DELETED
    }
}
