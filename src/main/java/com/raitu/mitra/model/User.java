package com.raitu.mitra.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;  // Primary login identifier (Indian farmers use phone)

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String village;
    private String district;
    private String state;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "rating", columnDefinition = "DECIMAL(3,1) DEFAULT 0.0")
    private Double rating;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Enumerated(EnumType.STRING)
    private Role role = Role.FARMER;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<Listing> listings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role {
        FARMER, BUYER, ADMIN
    }
}
