package com.servicepoint.core.controller;

import com.servicepoint.core.dto.ProviderWithUser;
import com.servicepoint.core.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @GetMapping
    public List<ProviderWithUser> getProviders() {
        return providerService.getProviders();
    }
}