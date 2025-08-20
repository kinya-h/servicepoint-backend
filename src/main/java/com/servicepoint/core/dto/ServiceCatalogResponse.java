package com.servicepoint.core.dto;

public record ServiceCatalogResponse(
        Integer serviceId,
        String name,
        String description,
        String category,
        Double price,
        String pricingType,
        String availability,
        String level,
        String subject,
        ProviderInfo provider

) {}
