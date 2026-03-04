package com.raitu.mitra.repository;

import com.raitu.mitra.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findFirstByDistrictOrderByRecordedAtDesc(String district);
    Optional<WeatherData> findFirstByDistrictAndStateOrderByRecordedAtDesc(String district, String state);
}
