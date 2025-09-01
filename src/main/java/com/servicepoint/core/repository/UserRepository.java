package com.servicepoint.core.repository;

import com.servicepoint.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    @Query(value = """
        SELECT DISTINCT u.*, 
               ST_Distance(
                   ST_SetSRID(ST_MakePoint(u.longitude, u.latitude), 4326)::geography,
                   ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
               ) / 1609.34 as distance_miles
        FROM users u
        JOIN service_catalog sc ON u.user_id = sc.provider_user_id
        WHERE u.role = 'provider'
          AND u.latitude IS NOT NULL 
          AND u.longitude IS NOT NULL
          AND LOWER(sc.subject) LIKE LOWER(CONCAT('%', :subject, '%'))
          AND ST_DWithin(
              ST_SetSRID(ST_MakePoint(u.longitude, u.latitude), 4326)::geography,
              ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
              :radius * 1000
          )
          AND (:category IS NULL OR LOWER(sc.category) = LOWER(:category))
          AND (:level IS NULL OR LOWER(sc.level) = LOWER(:level))
          AND (:priceMin IS NULL OR sc.price >= :priceMin)
          AND (:priceMax IS NULL OR sc.price <= :priceMax)
          AND (:pricingType IS NULL OR LOWER(sc.pricing_type) = LOWER(:pricingType))
          AND (:minRating IS NULL OR u.rating >= :minRating)
        ORDER BY distance_miles ASC, u.rating DESC NULLS LAST, sc.price ASC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<User> findProvidersNearbyByServiceWithFilters(
            @Param("subject") String subject,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") String category,
            @Param("level") String level,
            @Param("priceMin") Double priceMin,
            @Param("priceMax") Double priceMax,
            @Param("pricingType") String pricingType,
            @Param("minRating") Double minRating,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset
    );

    @Query(value = """
        SELECT COUNT(DISTINCT u.user_id)
        FROM users u
        JOIN service_catalog sc ON u.user_id = sc.provider_user_id
        WHERE u.role = 'provider'
          AND u.latitude IS NOT NULL 
          AND u.longitude IS NOT NULL
          AND LOWER(sc.subject) LIKE LOWER(CONCAT('%', :subject, '%'))
          AND ST_DWithin(
              ST_SetSRID(ST_MakePoint(u.longitude, u.latitude), 4326)::geography,
              ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
              :radius * 1000
          )
          AND (:category IS NULL OR LOWER(sc.category) = LOWER(:category))
          AND (:level IS NULL OR LOWER(sc.level) = LOWER(:level))
          AND (:priceMin IS NULL OR sc.price >= :priceMin)
          AND (:priceMax IS NULL OR sc.price <= :priceMax)
          AND (:pricingType IS NULL OR LOWER(sc.pricing_type) = LOWER(:pricingType))
          AND (:minRating IS NULL OR u.rating >= :minRating)
        """, nativeQuery = true)
    Long countProvidersNearbyByServiceWithFilters(
            @Param("subject") String subject,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") String category,
            @Param("level") String level,
            @Param("priceMin") Double priceMin,
            @Param("priceMax") Double priceMax,
            @Param("pricingType") String pricingType,
            @Param("minRating") Double minRating
    );
}
