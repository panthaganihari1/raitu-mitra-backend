package com.raitu.mitra.controller;

import com.raitu.mitra.model.User;
import com.raitu.mitra.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByPhone(userDetails.getUsername())
            .map(user -> ResponseEntity.ok(Map.of("success", true, "data", Map.of(
            ))))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateProfile(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody UpdateProfileRequest req
    ) {
        User user = userRepository.findByPhone(userDetails.getUsername()).orElseThrow();

        if (req.name() != null && !req.name().isBlank()) user.setName(req.name());
        if (req.village() != null) user.setVillage(req.village());
        if (req.district() != null) user.setDistrict(req.district());
        if (req.state() != null) user.setState(req.state());
        if (req.email() != null) user.setEmail(req.email());

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated successfully"));
    }

    @GetMapping("/{id}/public")
    @Operation(summary = "Get public profile of any user (for seller info)")
    public ResponseEntity<?> getPublicProfile(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> ResponseEntity.ok(Map.of("success", true, "data", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "village", user.getVillage() != null ? user.getVillage() : "",
                "district", user.getDistrict() != null ? user.getDistrict() : "",
                "rating", user.getRating(),
                "totalReviews", user.getTotalReviews()
                // NOTE: Phone is hidden in public profile — shared only through listing contact button
            ))))
            .orElse(ResponseEntity.notFound().build());
    }

    public record UpdateProfileRequest(String name, String village, String district, String state, String email) {}
}
