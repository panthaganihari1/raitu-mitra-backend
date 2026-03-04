# 🌾 రైతు మిత్ర — Raitu Mitra Backend

Java Spring Boot REST API for the Raitu Mitra rural farmer app.

---

## 🏗️ Tech Stack

| Layer       | Technology           |
|-------------|----------------------|
| Language    | Java 17              |
| Framework   | Spring Boot 3.2      |
| Database    | MySQL 8              |
| Auth        | JWT (JJWT 0.12)      |
| Security    | Spring Security      |
| ORM         | Spring Data JPA      |
| Docs        | Swagger / OpenAPI 3  |
| Build       | Maven                |

---

## 📦 Project Structure

```
src/main/java/com/raitu/mitra/
├── RaituMitraApplication.java     # Main entry point
├── config/
│   ├── SecurityConfig.java        # JWT filter + Spring Security
│   ├── JwtUtil.java               # Token generation/validation
│   ├── SwaggerConfig.java         # OpenAPI docs setup
│   └── DataSeeder.java            # Sample data on startup
├── controller/
│   ├── AuthController.java        # /auth/register, /auth/login
│   ├── ListingController.java     # /listings — Buy/Sell CRUD
│   ├── LivestockPriceController.java  # /livestock-prices
│   ├── CropPriceController.java   # /crop-prices
│   ├── WeatherController.java     # /weather
│   ├── DashboardController.java   # /dashboard (home screen)
│   └── UserController.java        # /users/me (profile)
├── model/
│   ├── User.java                  # Farmer entity
│   ├── Listing.java               # Buy/Sell listing entity
│   ├── LivestockPrice.java        # Daily animal prices
│   ├── CropPrice.java             # Daily crop mandi rates
│   └── WeatherData.java           # District weather
├── repository/                    # JPA repositories
├── dto/                           # Request/Response DTOs
└── exception/
    └── GlobalExceptionHandler.java
```

---

## 🚀 Setup & Run

### 1. Prerequisites
- Java 17+
- MySQL 8 running on localhost
- Maven 3.8+

### 2. Database Setup
```sql
CREATE DATABASE raitu_mitra_db;
CREATE USER 'raitu'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON raitu_mitra_db.* TO 'raitu'@'localhost';
```

### 3. Configure application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/raitu_mitra_db
spring.datasource.username=raitu
spring.datasource.password=your_password
```

### 4. Run
```bash
mvn clean install
mvn spring-boot:run
```

Server starts at: **http://localhost:8080/api/v1**  
Swagger UI: **http://localhost:8080/api/v1/swagger-ui.html**

---

## 📡 API Endpoints

### 🔐 Auth (Public)
| Method | Endpoint         | Description          |
|--------|-----------------|----------------------|
| POST   | /auth/register  | Register new farmer  |
| POST   | /auth/login     | Login with phone+pwd |

### 📢 Listings (Buy/Sell Animals)
| Method | Endpoint             | Auth | Description                    |
|--------|---------------------|------|--------------------------------|
| GET    | /listings            | ❌   | Get all listings (paginated)   |
| GET    | /listings/{id}       | ❌   | Get listing by ID              |
| GET    | /listings/nearby     | ❌   | Get listings near lat/lng      |
| GET    | /listings/my         | ✅   | Get my listings                |
| POST   | /listings            | ✅   | Create new listing             |
| PUT    | /listings/{id}       | ✅   | Update listing                 |
| PATCH  | /listings/{id}/sold  | ✅   | Mark as sold                   |
| DELETE | /listings/{id}       | ✅   | Delete listing                 |

**Query params for GET /listings:**
- `type` = BUY | SELL
- `category` = HEN | GOAT | COW | BUFFALO | PIG | SHEEP | DUCK | OTHER
- `district` = Guntur, Kurnool, etc.
- `keyword` = search text
- `page`, `size` = pagination

### 🐐 Livestock Prices
| Method | Endpoint                          | Auth  |
|--------|----------------------------------|-------|
| GET    | /livestock-prices/today           | ❌    |
| GET    | /livestock-prices/market/{market} | ❌    |
| GET    | /livestock-prices/category/{cat}  | ❌    |
| POST   | /livestock-prices                 | Admin |
| PUT    | /livestock-prices/{id}            | Admin |

### 🌾 Crop Prices
| Method | Endpoint                        | Auth  |
|--------|--------------------------------|-------|
| GET    | /crop-prices/today              | ❌    |
| GET    | /crop-prices/market/{market}    | ❌    |
| GET    | /crop-prices/district/{dist}    | ❌    |
| GET    | /crop-prices/crop/{name}/history| ❌    |
| POST   | /crop-prices                    | Admin |
| PUT    | /crop-prices/{id}               | Admin |

### 🌤️ Weather
| Method | Endpoint              | Auth  |
|--------|----------------------|-------|
| GET    | /weather/{district}   | ❌    |
| POST   | /weather              | Admin |

### 🏠 Dashboard
| Method | Endpoint    | Auth | Description                          |
|--------|------------|------|--------------------------------------|
| GET    | /dashboard  | ✅   | All home screen data in single call  |

**Query params:** `lat`, `lng` for location-based nearby listings

### 👤 Users
| Method | Endpoint           | Auth |
|--------|--------------------|------|
| GET    | /users/me          | ✅   |
| PUT    | /users/me          | ✅   |
| GET    | /users/{id}/public | ❌   |

---

## 🔑 Authentication

All protected endpoints require header:
```
Authorization: Bearer <jwt_token>
```

**Default test accounts (seeded on startup):**

| Phone       | Password    | Role   |
|-------------|-------------|--------|
| 9000000001  | admin123    | ADMIN  |
| 9876543210  | farmer123   | FARMER |

---

## 🔗 Mobile App Integration Example

```kotlin
// Android Retrofit call
@POST("auth/login")
suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

@GET("dashboard")
suspend fun getDashboard(
    @Header("Authorization") token: String,
    @Query("lat") lat: Double?,
    @Query("lng") lng: Double?
): Response<DashboardResponse>
```

---

## 🌐 Production Notes

- Replace MySQL with managed DB (AWS RDS / PlanetScale)
- Integrate OpenWeatherMap API or IMD for real weather
- Add FCM push notifications for listing inquiries
- Add image upload to AWS S3 / Cloudinary
- Use Redis for caching daily prices
- Enable HTTPS / SSL in production
