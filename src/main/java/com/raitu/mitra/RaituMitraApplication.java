package com.raitu.mitra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RaituMitraApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaituMitraApplication.class, args);
        System.out.println("""
            ==========================================
             🌾 Raitu Mitra Backend Started!
             📍 API: http://localhost:8080/api/v1
             📚 Docs: http://localhost:8080/api/v1/swagger-ui.html
            ==========================================
            """);
    }
}
