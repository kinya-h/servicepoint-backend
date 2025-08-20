package com.servicepoint.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    @JsonIgnore
    private User provider;

    @ManyToOne
    @JoinColumn(name = "service_id")
    @JsonIgnore
    private ServiceCatalog service;

    @Column(nullable = false)
    private Timestamp bookingDate;

    @Column(nullable = false)
    private Timestamp serviceDateTime;

    @Column(nullable = false)
    private String status; // pending, confirmed, completed, cancelled

    private String notes;

    @Column(nullable = false)
    private Double priceAtBooking;

    @Column(nullable = false)
    private String pricingTypeAtBooking; // hourly, per_work

}
