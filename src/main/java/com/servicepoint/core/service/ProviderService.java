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
        user.setUserId((Integer) result[0]);
        user.setUsername((String) result[1]);
        user.setEmail((String) result[2]);
        user.setPasswordHash((String) result[3]);
        user.setUsername((String) result[4]);
        user.setPhoneNumber((String) result[5]);
        user.setRole((String) result[6]);
        user.setLocation((String) result[7]);
        user.setLatitude((Double) result[8]);
        user.setLongitude((Double) result[9]);
        user.setRating((Double) result[10]);
        user.setReviewCount((Integer) result[11]);
        user.setLastLogin((java.sql.Timestamp) result[12]);
        user.setCreatedAt((java.sql.Timestamp) result[13]);
        user.setUpdatedAt((java.sql.Timestamp) result[14]);
        user.setProfilePicture((String) result[15]);
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