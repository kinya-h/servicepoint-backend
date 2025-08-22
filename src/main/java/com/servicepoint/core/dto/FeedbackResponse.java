package com.servicepoint.core.dto;

import java.sql.Timestamp;

public record FeedbackResponse (
    SimpleBookingInfo booking,
    CustomerInfo customer,
    ProviderInfo provider,
    String comments,
    Timestamp submissionDate ) {}