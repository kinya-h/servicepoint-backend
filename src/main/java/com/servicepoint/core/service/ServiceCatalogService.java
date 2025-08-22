package com.servicepoint.core.service;

import com.servicepoint.core.dto.NewServiceRequest;
import com.servicepoint.core.dto.ServiceCatalogResponse;
import com.servicepoint.core.dto.UpdateServiceRequest;
import com.servicepoint.core.model.ServiceCatalog;

import java.util.List;
import java.util.Optional;

public interface ServiceCatalogService {
    List<ServiceCatalogResponse> findAllServices();
    List<ServiceCatalogResponse> findServiceById(Integer serviceId);
    ServiceCatalogResponse saveService(NewServiceRequest service);
    void deleteService(Integer serviceId);
    List<ServiceCatalog> findServicesByProviderId(Integer providerId);
    ServiceCatalogResponse updateService(Integer serviceId, UpdateServiceRequest request);
}