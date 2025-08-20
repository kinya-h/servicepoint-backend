package com.servicepoint.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private long timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.error = "Bad Request";
        this.timestamp = System.currentTimeMillis();
    }
}