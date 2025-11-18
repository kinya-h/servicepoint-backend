package com.servicepoint.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Setter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter() {
        // Default constructor
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

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
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Extract roles from token or fallback to userDetails
                    Collection<? extends GrantedAuthority> authorities = extractAuthoritiesFromToken(jwt, userDetails);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, authorities);

                    System.out.println("DEBUG >> Authenticated user: " + username +
                            " with authorities: " + authorities);

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                System.err.println("JWT Filter Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> extractAuthoritiesFromToken(String token, UserDetails userDetails) {
        try {
            String roles = jwtUtil.extractRoles(token);
            if (roles != null && !roles.isEmpty()) {
                return Arrays.stream(roles.split(","))
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Failed to extract roles from token, using userDetails authorities");
        }

        // Fallback to userDetails authorities
        return userDetails.getAuthorities();
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