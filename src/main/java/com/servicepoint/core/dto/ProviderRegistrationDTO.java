package com.servicepoint.core.dto;

import com.servicepoint.core.model.ProviderRegistration;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProviderRegistrationDTO {
    private Integer registrationId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String location;
    private Double latitude;
    private Double longitude;
    private ProviderRegistration.RegistrationStatus status;
    private List<ProviderDocumentDTO> documents;
    private String rejectionReason;
    private Timestamp submittedAt;
    private Timestamp reviewedAt;
    private Integer reviewedBy;

    public static ProviderRegistrationDTO fromEntity(ProviderRegistration registration) {
        ProviderRegistrationDTO dto = new ProviderRegistrationDTO();
        dto.setRegistrationId(registration.getRegistrationId());
        dto.setFirstName(registration.getFirstName());
        dto.setLastName(registration.getLastName());
        dto.setEmail(registration.getEmail());
        dto.setPhoneNumber(registration.getPhoneNumber());
        dto.setLocation(registration.getLocation());
        dto.setLatitude(registration.getLatitude());
        dto.setLongitude(registration.getLongitude());
        dto.setStatus(registration.getStatus());
        dto.setRejectionReason(registration.getRejectionReason());
        dto.setSubmittedAt(registration.getSubmittedAt());
        dto.setReviewedAt(registration.getReviewedAt());
        dto.setReviewedBy(registration.getReviewedBy());

        // Convert documents to DTOs to avoid circular references
        if (registration.getDocuments() != null) {
            dto.setDocuments(registration.getDocuments().stream()
                    .map(ProviderDocumentDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}