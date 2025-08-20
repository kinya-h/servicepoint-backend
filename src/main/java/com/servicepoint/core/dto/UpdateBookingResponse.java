package com.servicepoint.core.dto;

import java.sql.Timestamp;

public record UpdateBookingResponse(

        Integer bookingId,
        Timestamp serviceDateTime,
        String status,
        String notes,
        Double priceAtBooking,
        String pricingTypeAtBooking,
        CustomerInfo customer,
        ProviderInfo provider

) {}
