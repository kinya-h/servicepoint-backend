package com.servicepoint.core.dto;

import com.servicepoint.core.model.User;

public interface ProviderProjection {
    User getUser();
    Double getDistanceMiles();
    Double getMinPrice();
}