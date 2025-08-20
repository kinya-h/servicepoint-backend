package com.servicepoint.core.repository;

import com.servicepoint.core.model.CommunicationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunicationPreferencesRepository extends JpaRepository<CommunicationPreferences, Integer> {
    Optional<CommunicationPreferences> findByUserUserId(Integer userId);
}