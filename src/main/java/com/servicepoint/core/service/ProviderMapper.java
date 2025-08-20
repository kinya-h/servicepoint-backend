package com.servicepoint.core.service;

import com.servicepoint.core.dto.ProviderInfo;
import com.servicepoint.core.dto.ServiceInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProviderMapper {

//    public ProviderInfo toDTO(Provider provider) {
//        ProviderInfo dto = new ProviderInfo();
//        dto.setId(provider.getId());
//        dto.setUsername(provider.getUsername());
//        dto.setEmail(provider.getEmail()); // skip if sensitive
//        dto.setProfilePicture(provider.getProfilePicture());
//
//        List<ServiceInfo> serviceDTOs = provider.getServices().stream()
//                .map(service -> {
//                    ServiceInfo sDto = new ServiceInfo();
//                    sDto.setId(service.getId());
//                    sDto.setName(service.getName());
//                    sDto.setDescription(service.getDescription());
//                    sDto.setPrice(service.getPrice());
//                    return sDto;
//                })
//                .collect(Collectors.toList());
//
//        dto.setServices(serviceDTOs);
//
//        return dto;
//    }
}
