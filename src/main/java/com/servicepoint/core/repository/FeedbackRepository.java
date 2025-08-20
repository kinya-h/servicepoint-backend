package com.servicepoint.core.repository;

import com.servicepoint.core.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByProviderUserId(Integer providerId);
}