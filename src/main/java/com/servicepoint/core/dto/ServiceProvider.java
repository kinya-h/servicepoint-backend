package com.servicepoint.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ServiceProvider {

    private Integer id;
    private String username;
    private String email;
    private String role;
    ServiceInfo service;
}
