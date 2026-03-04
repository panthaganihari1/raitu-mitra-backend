package com.raitu.mitra.repository;

import com.raitu.mitra.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    // Find all active listings
    Page<Listing> findByStatus(Listing.ListingStatus status, Pageable pageable);

    // Find by type (BUY or SELL)
    Page<Listing> findByTypeAndStatus(Listing.ListingType type, Listing.ListingStatus status, Pageable pageable);

    // Find by animal category
    Page<Listing> findByCategoryAndStatus(Listing.AnimalCategory category, Listing.ListingStatus status, Pageable pageable);

    // Find by seller
    List<Listing> findBySeller_Id(Long sellerId);

    // Search by keyword in title or description
    @Query("SELECT l FROM Listing l WHERE l.status = 'ACTIVE' AND " +
           "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Listing> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Find nearby listings (by district)
    Page<Listing> findByDistrictAndStatus(String district, Listing.ListingStatus status, Pageable pageable);

    // Find by district and category
    Page<Listing> findByDistrictAndCategoryAndStatus(
        String district, Listing.AnimalCategory category, Listing.ListingStatus status, Pageable pageable
    );

    // Nearby by coordinates (within radius in km)
    @Query(value = """
        SELECT * FROM listings l
        WHERE l.status = 'ACTIVE'
        AND l.latitude IS NOT NULL
        AND (6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude))
             * cos(radians(l.longitude) - radians(:lng))
             + sin(radians(:lat)) * sin(radians(l.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude))
             * cos(radians(l.longitude) - radians(:lng))
             + sin(radians(:lat)) * sin(radians(l.latitude))))
        """, nativeQuery = true)
    List<Listing> findNearbyListings(@Param("lat") Double lat, @Param("lng") Double lng,
                                     @Param("radiusKm") Double radiusKm);
}
