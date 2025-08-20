package com.servicepoint.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Setter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Setter methods for dependency injection
    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;

    // Constructor injection to avoid circular dependency
    public JwtAuthenticationFilter() {
        // Default constructor
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Get dependencies from application context if not injected
        if (jwtUtil == null || userDetailsService == null) {
            initializeDependencies();
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Invalid token, continue with filter chain
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    System.out.println("DEBUG >> Authenticated user: " + username +
                            " with authorities: " + userDetails.getAuthorities());

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                // User not found or other error, continue without authentication
            }
        }
        filterChain.doFilter(request, response);
    }

    private void initializeDependencies() {
        if (jwtUtil == null) {
            jwtUtil = getApplicationContext().getBean(JwtUtil.class);
        }
        if (userDetailsService == null) {
            userDetailsService = getApplicationContext().getBean(UserDetailsService.class);
        }
    }

    private org.springframework.context.ApplicationContext getApplicationContext() {
        return org.springframework.web.context.support.WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
    }
}
