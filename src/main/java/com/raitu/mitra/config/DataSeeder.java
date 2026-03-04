package com.raitu.mitra.config;

import com.raitu.mitra.model.*;
import com.raitu.mitra.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LivestockPriceRepository livestockPriceRepository;
    private final CropPriceRepository cropPriceRepository;
    private final WeatherRepository weatherRepository;
    private final ListingRepository listingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // Already seeded

        System.out.println("🌱 Seeding initial data...");

        // ── Admin User ────────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
            .name("Admin")
            .phone("9000000001")
            .password(passwordEncoder.encode("admin123"))
            .village("Guntur")
            .district("Guntur")
            .state("Andhra Pradesh")
            .role(User.Role.ADMIN)
            .rating(5.0)
            .totalReviews(0)
            .isActive(true)
            .build());

        // ── Sample Farmer ─────────────────────────────────────────────────────
        User farmer = userRepository.save(User.builder()
            .name("రామారావు")
            .phone("9876543210")
            .password(passwordEncoder.encode("farmer123"))
            .village("Narasaraopet")
            .district("Guntur")
            .state("Andhra Pradesh")
            .role(User.Role.FARMER)
            .rating(4.8)
            .totalReviews(12)
            .isActive(true)
            .build());

        // ── Livestock Prices ──────────────────────────────────────────────────
        LocalDate today = LocalDate.now();

        livestockPriceRepository.save(LivestockPrice.builder()
            .category(Listing.AnimalCategory.HEN)
            .animalName("Country Hen").animalNameTelugu("నాటు కోడి")
            .pricePerUnit(350.0).previousPrice(330.0).unit("each")
            .market("Guntur").district("Guntur").state("Andhra Pradesh")
            .priceDate(today).isHot(true).build());

        livestockPriceRepository.save(LivestockPrice.builder()
            .category(Listing.AnimalCategory.GOAT)
            .animalName("Nellore Goat").animalNameTelugu("నెల్లూరు మేక")
            .pricePerUnit(6500.0).previousPrice(6700.0).unit("each")
            .market("Kurnool").district("Kurnool").state("Andhra Pradesh")
            .priceDate(today).isHot(false).build());

        livestockPriceRepository.save(LivestockPrice.builder()
            .category(Listing.AnimalCategory.COW)
            .animalName("Desi Cow").animalNameTelugu("నాటు ఆవు")
            .pricePerUnit(45000.0).previousPrice(44500.0).unit("each")
            .market("Vijayawada").district("Krishna").state("Andhra Pradesh")
            .priceDate(today).isHot(false).build());

        livestockPriceRepository.save(LivestockPrice.builder()
            .category(Listing.AnimalCategory.BUFFALO)
            .animalName("Murrah Buffalo").animalNameTelugu("గేదె")
            .pricePerUnit(80000.0).previousPrice(79000.0).unit("each")
            .market("Nellore").district("Nellore").state("Andhra Pradesh")
            .priceDate(today).isHot(false).build());

        // ── Crop Prices ───────────────────────────────────────────────────────
        cropPriceRepository.save(CropPrice.builder()
            .cropName("Red Chilli").cropNameTelugu("ఎర్ర మిరపకాయ")
            .emoji("🌶️").grade("Grade A")
            .pricePerKg(280.0).previousPrice(266.0)
            .minPrice(240.0).maxPrice(310.0).modalPrice(280.0)
            .market("Guntur Market").district("Guntur").state("Andhra Pradesh")
            .priceDate(today).build());

        cropPriceRepository.save(CropPrice.builder()
            .cropName("Onion").cropNameTelugu("ఉల్లిపాయ")
            .emoji("🧅").grade("Medium")
            .pricePerKg(22.0).previousPrice(22.7)
            .minPrice(18.0).maxPrice(26.0).modalPrice(22.0)
            .market("Kurnool Market").district("Kurnool").state("Andhra Pradesh")
            .priceDate(today).build());

        cropPriceRepository.save(CropPrice.builder()
            .cropName("Turmeric").cropNameTelugu("పసుపు")
            .emoji("🌿").grade("Grade A")
            .pricePerKg(180.0).previousPrice(166.0)
            .minPrice(160.0).maxPrice(200.0).modalPrice(180.0)
            .market("Nizamabad Market").district("Nizamabad").state("Telangana")
            .priceDate(today).build());

        cropPriceRepository.save(CropPrice.builder()
            .cropName("Maize").cropNameTelugu("మొక్కజొన్న")
            .emoji("🌽")
            .pricePerKg(24.0).previousPrice(23.5)
            .minPrice(20.0).maxPrice(27.0).modalPrice(24.0)
            .market("Warangal Market").district("Warangal").state("Telangana")
            .priceDate(today).build());

        // ── Weather Data ──────────────────────────────────────────────────────
        weatherRepository.save(WeatherData.builder()
            .district("Guntur").state("Andhra Pradesh")
            .latitude(16.3007).longitude(80.4428)
            .temperature(34.0).feelsLike(38.0).humidity(72.0)
            .windSpeed(18.0).windDirection("NE")
            .rainProbability(20.0).rainfall(0.0)
            .condition("Partly Cloudy").conditionTelugu("మేఘావృతంగా ఉంటుంది")
            .icon("🌤️")
            .farmingAdvisory("Reduce irrigation today. Good day for harvesting dry crops.")
            .farmingAdvisoryTelugu("మేఘావృతంగా ఉంటుంది — నీటి పారుదల తగ్గించండి")
            .recordedAt(LocalDateTime.now())
            .build());

        // ── Sample Listings ───────────────────────────────────────────────────
        listingRepository.save(Listing.builder()
            .title("10 Country Hens (Female)").animalName("Country Hen")
            .description("Healthy hens, 6 months old, vaccinated")
            .type(Listing.ListingType.SELL).category(Listing.AnimalCategory.HEN)
            .quantity(10).price(350.0).isNegotiable(true)
            .age("6 months").healthStatus("Vaccinated")
            .village("Narasaraopet").district("Guntur").state("Andhra Pradesh")
            .latitude(16.2396).longitude(80.0495)
            .status(Listing.ListingStatus.ACTIVE).viewCount(24)
            .seller(farmer).build());

        listingRepository.save(Listing.builder()
            .title("Wanted: 2 Pregnant Goats").animalName("Nellore Goat")
            .description("Looking for pregnant goats, Nellore breed preferred")
            .type(Listing.ListingType.BUY).category(Listing.AnimalCategory.GOAT)
            .quantity(2).price(7000.0).isNegotiable(true)
            .village("Macherla").district("Guntur").state("Andhra Pradesh")
            .status(Listing.ListingStatus.ACTIVE).viewCount(8)
            .seller(admin).build());

        System.out.println("✅ Seed data inserted successfully!");
    }
}
