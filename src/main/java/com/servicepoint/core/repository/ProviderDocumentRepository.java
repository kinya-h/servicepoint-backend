package com.servicepoint.core.repository;

import com.servicepoint.core.model.ProviderDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderDocumentRepository extends JpaRepository<ProviderDocument, Integer> {
    List<ProviderDocument> findByRegistration_RegistrationId(Integer registrationId);
}