package com.servicepoint.core.service;

import com.servicepoint.core.dto.ProviderWithUser;
import com.servicepoint.core.dto.ServiceInfo;
import com.servicepoint.core.dto.UserResponse;
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
    private ServiceCatalogRepository serviceRepository;

    public List<ProviderWithUser> getProviders() {
        List<User> providers = userRepository.findAll().stream()
                .filter(user -> "provider".equals(user.getRole()))
                .toList();

        return providers.stream().map(provider -> {
            ServiceCatalog providerService = serviceRepository.findByProviderUserId(provider.getUserId()).stream()
                    .findFirst().orElse(null);

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
                        providerService.getPricingType(),
                        providerService.getDescription(),
                        providerService.getAvailability(),
                        providerService.getPrice(),
                        providerService.getCategory(),
                        providerService.getLevel(),
                        providerService.getSubject()
                );
            }

            return new ProviderWithUser(provider.getUserId(), userDTO, serviceInfoDTO);
        }).collect(Collectors.toList());
    }
}