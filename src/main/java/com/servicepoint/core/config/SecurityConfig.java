package com.servicepoint.core.config;

import com.servicepoint.core.security.JwtAuthenticationFilter;
import com.servicepoint.core.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService uds) {
        JwtAuthenticationFilter f = new JwtAuthenticationFilter();
        f.setJwtUtil(jwtUtil);
        f.setUserDetailsService(uds);
        return f;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider authenticationProvider,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/tokens/renew_access").permitAll()
                        .requestMatchers("/api/tokens/validate").permitAll()

                        // Health check and documentation endpoints
                        .requestMatchers("/health", "/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Provider Registration - public submission, admin-only management
                        .requestMatchers("/api/provider-registration/request-otp").permitAll()
                        .requestMatchers("/api/provider-registration/submit").permitAll()

                        // Provider Registration Admin endpoints - require ADMIN role
                        .requestMatchers("/api/provider-registration/all").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/provider-registration/pending").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/provider-registration/approved").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/provider-registration/rejected").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/provider-registration/approve/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/provider-registration/reject/**").hasAuthority("ROLE_ADMIN")

                        // Provider Auth - public status check, authenticated login
                        .requestMatchers("/api/provider-auth/status").permitAll()
                        .requestMatchers("/api/provider-auth/login").permitAll()

                        // Admin endpoints - require ADMIN role
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/users/all").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/uploads/provider-documents/**").hasAuthority("ROLE_ADMIN")

                        // User endpoints - require authenticated users
                        .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_PROVIDER", "ROLE_CUSTOMER")
                        .requestMatchers("/api/services/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_PROVIDER", "ROLE_CUSTOMER")
                        .requestMatchers("/api/bookings/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_PROVIDER", "ROLE_CUSTOMER")
                        .requestMatchers("/api/feedback/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_PROVIDER", "ROLE_CUSTOMER")
                        .requestMatchers("/api/providers/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_PROVIDER", "ROLE_CUSTOMER")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}