package com.servicepoint.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Integer customerId;
    private Integer providerId;
    private Integer serviceId;
    private Timestamp bookingDate;
    private Timestamp serviceDateTime;
    private String status;
    private String notes;
    private Double priceAtBooking;
    private Double totalPrice;
    private String pricingTypeAtBooking;
}
