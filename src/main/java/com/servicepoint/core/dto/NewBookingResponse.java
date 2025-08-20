package com.servicepoint.core.dto;


import java.sql.Timestamp;

public record NewBookingResponse(
        Integer bookingId,
        Timestamp bookingDate,
        Timestamp serviceDateTime,
        String status,
        String notes,
        Double priceAtBooking,
        String pricingTypeAtBooking,
        CustomerInfo customer,
        ProviderInfo provider,
        ServiceInfo serviceInfo

) {}
