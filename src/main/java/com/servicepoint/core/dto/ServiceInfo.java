package com.servicepoint.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {
    private Integer serviceId; // TODO:: change later to id to maintain consistency
    private String pricingType;
    private String description;
    private String availability;
    private Double price;
    private String category;
    private String level;
    private String subject;
}