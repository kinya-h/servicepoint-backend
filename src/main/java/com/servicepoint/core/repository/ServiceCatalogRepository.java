package com.servicepoint.core.repository;

import com.servicepoint.core.model.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Integer> {
    List<ServiceCatalog> findByProviderUserId(Integer providerId);
}