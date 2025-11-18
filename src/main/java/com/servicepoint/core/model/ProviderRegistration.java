package com.servicepoint.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "provider_registrations")
public class ProviderRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer registrationId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String phoneNumber;
    private String location;
    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderDocument> documents = new ArrayList<>();

    private String rejectionReason;

    @Column(nullable = false)
    private String otpCode; // Store OTP used during registration

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp submittedAt;

    private Timestamp reviewedAt;
    private Integer reviewedBy; // Admin user ID

    public enum RegistrationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
