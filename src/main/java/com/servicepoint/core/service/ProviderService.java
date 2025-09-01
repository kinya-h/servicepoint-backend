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

    // Method using LocationSearchRequest DTO
    public List<ProviderWithUser> getProvidersNearbyByService(LocationSearchRequest request) {
        List<User> providers = userRepository.findProvidersNearbyByServiceWithFilters(
                request.getSubject(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getCategory(),
                request.getLevel(),
                request.getPriceMin(),
                request.getPriceMax(),
                request.getPricingType(),
                request.getMinRating(),
                request.getLimit(),
                request.getOffset()
        );

        return providers.stream().map(this::mapToProviderWithUser)
                .collect(Collectors.toList());
    }

    // Enhanced method returning full response with metadata
    public LocationSearchResponse searchProvidersNearbyByService(LocationSearchRequest request) {
        List<ProviderWithUser> providers = getProvidersNearbyByService(request);

        Long totalCount = userRepository.countProvidersNearbyByServiceWithFilters(
                request.getSubject(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getCategory(),
                request.getLevel(),
                request.getPriceMin(),
                request.getPriceMax(),
                request.getPricingType(),
                request.getMinRating()
        );

        return new LocationSearchResponse(
                providers,
                totalCount.intValue(),
                request.getLimit(),
                request.getOffset(),
                request.getRadius(),
                request.getSubject(),
                null // metadata will be set in controller
        );
    }

    // Advanced search with all filters
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

    // Helper method to map User to ProviderWithUser
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
                provider.getDistanceMiles(),
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