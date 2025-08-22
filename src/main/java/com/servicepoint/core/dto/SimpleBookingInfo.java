package com.servicepoint.core.dto;

import java.sql.Timestamp;

public record SimpleBookingInfo (
    Integer id,
    Timestamp bookingDate,
    Timestamp serviceDateTime,
    String status,
    String notes,
    Double priceAtBooking,
    String pricingTypeAtBooking )

{}
