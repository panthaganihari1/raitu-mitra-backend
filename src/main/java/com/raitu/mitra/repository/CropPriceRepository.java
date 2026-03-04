package com.raitu.mitra.repository;

import com.raitu.mitra.model.CropPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CropPriceRepository extends JpaRepository<CropPrice, Long> {

    List<CropPrice> findByPriceDateOrderByCropName(LocalDate date);

    List<CropPrice> findByMarketAndPriceDateOrderByCropName(String market, LocalDate date);

    List<CropPrice> findByDistrictAndPriceDate(String district, LocalDate date);

    List<CropPrice> findByCropNameOrderByPriceDateDesc(String cropName);
}
