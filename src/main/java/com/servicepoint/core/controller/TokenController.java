package com.servicepoint.core.controller;

import com.servicepoint.core.dto.*;
import com.servicepoint.core.model.User;
import com.servicepoint.core.security.JwtUtil;
import com.servicepoint.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/renew_access")
    public ResponseEntity<?> renewAccessToken(@RequestBody RenewAccessTokenRequest request) {
        try {
            // Validate refresh token
            if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Refresh token is required"));
            }

            // Extract username/email from refresh token
            String email;
            try {
                email = jwtUtil.extractUsername(request.getRefreshToken());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid refresh token"));
            }

            // Check if refresh token is expired
            if (jwtUtil.extractExpiration(request.getRefreshToken()).before(new Date())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Refresh token has expired"));
            }

            // Find user by email
            Optional<User> userOptional = userService.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not found"));
            }

            User user = userOptional.get();

            // Validate refresh token against user
            Optional<User> userDetails = userService.findUserByEmail(email);
            if (!jwtUtil.validateToken(request.getRefreshToken(), String.valueOf(userDetails))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid refresh token"));
            }

            // Generate new access token
            String newAccessToken = jwtUtil.generateToken(email);

            // Calculate expiration time (15 minutes from now)
            long expirationTimeMs = 15 * 60 * 1000; // 15 minutes in milliseconds
            Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeMs);

            RenewAccessTokenResponse response = new RenewAccessTokenResponse(
                    newAccessToken,
                    expirationDate,
                    expirationTimeMs
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while renewing access token"));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody ValidateTokenRequest request) {
        try {
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Token is required"));
            }

            String email = jwtUtil.extractUsername(request.getToken());
            Date expiration = jwtUtil.extractExpiration(request.getToken());

            // Check if token is expired
            boolean isExpired = expiration.before(new Date());

            // Find user
            Optional<User> userOptional = userService.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not found"));
            }

            Optional<User> userDetails = userService.findUserByEmail(email);
            boolean isValid = jwtUtil.validateToken(request.getToken(), String.valueOf(userDetails));

            ValidateTokenResponse response = new ValidateTokenResponse(
                    isValid && !isExpired,
                    email,
                    expiration,
                    isExpired
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token"));
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeToken(@RequestBody RevokeTokenRequest request) {
        try {
            // For JWT tokens, we can't actually revoke them server-side without maintaining a blacklist
            // This is a placeholder for future implementation with token blacklisting

            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Token is required"));
            }

            // In a real implementation, you would:
            // 1. Add token to a blacklist/redis cache
            // 2. Check this blacklist in your JWT filter

            return ResponseEntity.ok(new RevokeTokenResponse("Token revoked successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while revoking token"));
        }
    }
}