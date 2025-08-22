package com.servicepoint.core.dto;

import com.servicepoint.core.model.Booking;
import com.servicepoint.core.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class FeedbackRequest {

    private Integer bookingId;
    private Integer customerId;
    private Integer providerId;
    private String comments;
    private Timestamp submissionDate;
}
