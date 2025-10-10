package com.servicepoint.core.service;

import com.servicepoint.core.dto.*;
import com.servicepoint.core.exception.ResourceNotFoundException;
import com.servicepoint.core.model.ServiceCatalog;
import com.servicepoint.core.model.User;
import com.servicepoint.core.repository.ServiceCatalogRepository;
import com.servicepoint.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ServiceCatalogService catalogService;

    @Autowired
    private ServiceCatalogRepository serviceRepository;

    public List<ProviderWithUser> getProviders() {
        List<User> providers = userRepository.findAll().stream()
                .filter(user -> "provider".equals(user.getRole()))
                .toList();

        return providers.stream().map(this::mapToProviderWithUser)
                .collect(Collectors.toList());
    }

    public List<ProviderWithUser> getProvidersNearbyByService(LocationSearchRequest request) {
        // If no coordinates provided, fall back to general search
        if (request.getLatitude() == null || request.getLongitude() == null) {
            return getProvidersByServiceWithoutLocation(request);
        }

        List<Object[]> results = userRepository.findProvidersNearbyByServiceWithFilters(
                request.getCategory(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getLimit(),
                request.getOffset()
        );

        return results.stream()
                .map(this::mapResultToProviderWithUser)
                .collect(Collectors.toList());
    }

    private List<ProviderWithUser> getProvidersByServiceWithoutLocation(LocationSearchRequest request) {
        List<Object[]> results = userRepository.findProvidersByServiceWithoutDistance(
                request.getCategory(),
                request.getLimit(),
                request.getOffset()
        );

        return results.stream()
                .map(this::mapResultToProviderWithUser)
                .collect(Collectors.toList());
    }

    public LocationSearchResponse searchProvidersNearbyByService(LocationSearchRequest request) {
        List<ProviderWithUser> providers = getProvidersNearbyByService(request);

        Long totalCount = userRepository.countProvidersNearbyByServiceWithFilters(
                request.getCategory(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius()
        );

        return new LocationSearchResponse(
                providers,
                totalCount.intValue(),
                request.getLimit(),
                request.getOffset(),
                request.getRadius(),
                request.getCategory(),
                null
        );
    }

    public LocationSearchResponse advancedSearchProviders(LocationSearchRequest request) {
        return searchProvidersNearbyByService(request);
    }

    public List<ServiceProvider> getServicesByProvider(Integer providerId) {
        var provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return catalogService.findServicesByProviderId(provider.getUserId())
                .stream()
                .map(service -> new ServiceProvider(
                        service.getProvider().getUserId(),
                        service.getProvider().getUsername(),
                        service.getProvider().getEmail(),
                        service.getProvider().getRole(),
                        new ServiceInfo(
                                service.getServiceId(),
                                service.getName(),
                                service.getDescription(),
                                service.getCategory(),
                                service.getAvailability(),
                                service.getPrice(),
                                service.getPricingType(),
                                service.getLevel(),
                                service.getSubject()
                        )
                ))
                .collect(Collectors.toList());
    }

    private ProviderWithUser mapResultToProviderWithUser(Object[] result) {
        User provider = extractUserFromResult(result);
        Double distanceMiles = (Double) result[16]; // distance_miles is at index 16

        // Get the service for this provider
        ServiceCatalog providerService = serviceRepository.findByProviderUserId(provider.getUserId())
                .stream()
                .findFirst()
                .orElse(null);

        UserResponse userDTO = new UserResponse(
                provider.getUserId(),
                provider.getUsername(),
                provider.getEmail(),
                provider.getRole(),
                provider.getProfilePicture(),
                provider.getLocation(),
                provider.getLatitude(),
                provider.getLongitude(),
                provider.getPhoneNumber(),
                provider.getRating(),
                provider.getReviewCount(),
                distanceMiles,
                provider.getLastLogin() != null ? provider.getLastLogin().toString() : null,
                provider.getCreatedAt().toString(),
                provider.getUpdatedAt().toString()
        );

        ServiceInfo serviceInfoDTO = null;
        if (providerService != null) {
            serviceInfoDTO = new ServiceInfo(
                    providerService.getServiceId(),
                    providerService.getName(),
                    providerService.getDescription(),
                    providerService.getCategory(),
                    providerService.getAvailability(),
                    providerService.getPrice(),
                    providerService.getPricingType(),
                    providerService.getLevel(),
                    providerService.getSubject()
            );
        }

        return new ProviderWithUser(provider.getUserId(), userDTO, serviceInfoDTO);
    }

    private User extractUserFromResult(Object[] result) {
        User user = new User();

        // Proper casting based on SQL result order
        user.setUserId(((Number) result[0]).intValue()); // user_id
        user.setEmail((String) result[1]); // email
        user.setPasswordHash((String) result[2]); // password_hash
        user.setUsername((String) result[3]); // username
        user.setPhoneNumber((String) result[4]); // phone_number
        user.setRole((String) result[5]); // role
        user.setLocation((String) result[6]); // location
        user.setLatitude(result[7] != null ? ((Number) result[7]).doubleValue() : null); // latitude
        user.setLongitude(result[8] != null ? ((Number) result[8]).doubleValue() : null); // longitude
        user.setRating(result[9] != null ? ((Number) result[9]).doubleValue() : null); // rating
        user.setReviewCount(result[10] != null ? ((Number) result[10]).intValue() : null); // review_count
        user.setLastLogin((java.sql.Timestamp) result[11]); // last_login
        user.setCreatedAt((java.sql.Timestamp) result[12]); // created_at
        user.setUpdatedAt((java.sql.Timestamp) result[13]); // updated_at
        user.setProfilePicture((String) result[14]); // profile_picture

        return user;
    }

    private ProviderWithUser mapToProviderWithUser(User provider) {
        ServiceCatalog providerService = serviceRepository.findByProviderUserId(provider.getUserId())
                .stream()
                .findFirst()
                .orElse(null);

        UserResponse userDTO = new UserResponse(
                provider.getUserId(),
                provider.getUsername(),
                provider.getEmail(),
                provider.getRole(),
                provider.getProfilePicture(),
                provider.getLocation(),
                provider.getLatitude(),
                provider.getLongitude(),
                provider.getPhoneNumber(),
                provider.getRating(),
                provider.getReviewCount(),
                null, // distance will be null for non-distance queries
                provider.getLastLogin() != null ? provider.getLastLogin().toString() : null,
                provider.getCreatedAt().toString(),
                provider.getUpdatedAt().toString()
        );

        ServiceInfo serviceInfoDTO = null;
        if (providerService != null) {
            serviceInfoDTO = new ServiceInfo(
                    providerService.getServiceId(),
                    providerService.getName(),
                    providerService.getDescription(),
                    providerService.getCategory(),
                    providerService.getAvailability(),
                    providerService.getPrice(),
                    providerService.getPricingType(),
                    providerService.getLevel(),
                    providerService.getSubject()
            );
        }

        return new ProviderWithUser(provider.getUserId(), userDTO, serviceInfoDTO);
    }
}