package com.servicepoint.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceCatalog service;

    @Column(nullable = false)
    private Timestamp bookingDate;

    @Column(nullable = false)
    private Timestamp serviceDateTime;

    @Column(nullable = false)
    private String status; // pending, confirmed, paid, in_progress, completed, cancelled

    // Snapshot of pricing at time of booking (important for historical records)
    @Column(nullable = false)
    private Double priceAtBooking;

    @Column(nullable = false)
    private String pricingTypeAtBooking; // hourly, per_work

    // Total price calculation (can include hours, additional fees, etc.)
    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double totalPrice = 0.0;

    private String notes;

    // Payment-related fields
    @Column(nullable = false)
    private String paymentStatus = "pending"; // pending, completed, failed, cancelled, refunded

    private String stripeSessionId;
    private String stripePaymentIntentId;
    private Timestamp paidAt;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    private Timestamp updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PrePersist
    protected void onCreate() {
        if (totalPrice == null) {
            totalPrice = 0.0;
        }
        if (paymentStatus == null) {
            paymentStatus = "pending";
        }

    }
}