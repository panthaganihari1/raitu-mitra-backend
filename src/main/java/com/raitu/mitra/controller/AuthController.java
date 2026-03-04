package com.raitu.mitra.controller;

import com.raitu.mitra.dto.AuthDTOs;
import com.raitu.mitra.model.User;
import com.raitu.mitra.repository.UserRepository;
import com.raitu.mitra.config.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register & Login APIs")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    @Operation(summary = "Register a new farmer")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTOs.RegisterRequest req) {

        if (userRepository.existsByPhone(req.getPhone())) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Phone number already registered"
            ));
        }

        User user = User.builder()
            .name(req.getName())
            .phone(req.getPhone())
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .village(req.getVillage())
            .district(req.getDistrict())
            .state(req.getState())
            .role(User.Role.FARMER)
            .rating(0.0)
            .totalReviews(0)
            .isActive(true)
            .build();

        user = userRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername(user.getPhone());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "నమోదు విజయవంతమైంది! (Registration successful!)",
            "data", AuthDTOs.AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .village(user.getVillage())
                .district(user.getDistrict())
                .role(user.getRole())
                .build()
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with phone number and password")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTOs.LoginRequest req) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getPhone(), req.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Invalid phone number or password"
            ));
        }

        User user = userRepository.findByPhone(req.getPhone()).orElseThrow();
        var userDetails = userDetailsService.loadUserByUsername(user.getPhone());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "లాగిన్ విజయవంతమైంది! (Login successful!)",
            "data", AuthDTOs.AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .village(user.getVillage())
                .district(user.getDistrict())
                .role(user.getRole())
                .build()
        ));
    }
}
