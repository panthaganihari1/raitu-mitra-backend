package com.raitu.mitra.repository;

import com.raitu.mitra.model.LivestockPrice;
import com.raitu.mitra.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LivestockPriceRepository extends JpaRepository<LivestockPrice, Long> {

    List<LivestockPrice> findByPriceDateOrderByCategory(LocalDate date);

    List<LivestockPrice> findByMarketAndPriceDate(String market, LocalDate date);

    Optional<LivestockPrice> findByCategoryAndMarketAndPriceDate(
        Listing.AnimalCategory category, String market, LocalDate date);

    List<LivestockPrice> findByCategoryOrderByPriceDateDesc(Listing.AnimalCategory category);
}
