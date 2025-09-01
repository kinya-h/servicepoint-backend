package com.servicepoint.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(length = 255)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // customer or provider

    private String profilePicture;

    private String location;

    private Double latitude;

    private Double longitude;

    private String phoneNumber;

    private Double rating;

    private Integer reviewCount;

    private Double distanceMiles;

    private Timestamp lastLogin;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Session> sessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PaymentPreference paymentPreferences;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CommunicationPreferences communicationPreferences;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ServiceCatalog> services;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Booking> customerBookings;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Booking> providerBookings;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Feedback> customerFeedbacks;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Feedback> providerFeedbacks;

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRole() {
        return this.role;
    }
}
