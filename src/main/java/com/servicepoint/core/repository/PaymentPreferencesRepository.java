package com.servicepoint.core.repository;

import com.servicepoint.core.model.PaymentPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentPreferencesRepository extends JpaRepository<PaymentPreferences, Integer> {
    Optional<PaymentPreferences> findByUserUserId(Integer userId);
}