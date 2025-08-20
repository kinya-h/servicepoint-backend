package com.servicepoint.core.dto;

public record UserInfo (
        Integer userId,
        String username,
        String email
) {}
