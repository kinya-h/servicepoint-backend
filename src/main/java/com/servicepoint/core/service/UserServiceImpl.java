package com.servicepoint.core.service;

import com.servicepoint.core.dto.LoginRequest;
import com.servicepoint.core.dto.LoginResponse;
import com.servicepoint.core.dto.RegisterRequest;
import com.servicepoint.core.dto.UserResponse;
import com.servicepoint.core.exception.ResourceNotFoundException;
import com.servicepoint.core.model.Session;
import com.servicepoint.core.model.User;
import com.servicepoint.core.repository.SessionRepository;
import com.servicepoint.core.repository.UserRepository;
import com.servicepoint.core.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserResponse createUser(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User with this username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public LoginResponse loginUser(LoginRequest request, HttpServletRequest httpRequest) {
        User user = findUserByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Update last login
        user.setLastLogin(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Create and save session
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setUserAgent(httpRequest.getHeader("User-Agent") != null ?
                httpRequest.getHeader("User-Agent") : "Unknown");
        session.setClientIp(getClientIpAddress(httpRequest));
        session.setIsBlocked(false);
        // Set refresh token expiration (7 days from now)
        session.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(7)));
        session.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // Save session first
        sessionRepository.save(session);

        // Return login response with tokens
        return new LoginResponse(accessToken, refreshToken, 900000L, 604800000L); // 15m, 7d
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username/email: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().toUpperCase()) // "provider" → "PROVIDER" → ROLE_PROVIDER
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    @Override
    public User saveUser(User user) {
        if (user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2a$")) {
            // Only encode if it's not already encoded
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        if (user.getUserId() == null) { // New user
            user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    /**
     * Get client IP address from HTTP request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, get the first one
            return xForwardedForHeader.split(",")[0].trim();
        }

        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty()) {
            return xRealIpHeader;
        }

        String xForwardedProtoHeader = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProtoHeader != null && !xForwardedProtoHeader.isEmpty()) {
            return request.getRemoteAddr();
        }

        return request.getRemoteAddr();
    }

    private UserResponse convertToDTO(User user) {
        UserResponse userDTO = new UserResponse();
        userDTO.setUserId(user.getUserId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setLocation(user.getLocation());
        userDTO.setLatitude(user.getLatitude());
        userDTO.setLongitude(user.getLongitude());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRating(user.getRating());
        userDTO.setReviewCount(user.getReviewCount());
        userDTO.setDistanceMiles(user.getDistanceMiles());
        userDTO.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : null);
        userDTO.setCreatedAt(user.getCreatedAt().toString());
        userDTO.setUpdatedAt(user.getUpdatedAt().toString());
        return userDTO;
    }
}