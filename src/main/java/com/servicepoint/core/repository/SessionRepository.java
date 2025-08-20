package com.servicepoint.core.repository;

import com.servicepoint.core.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, String> {
    List<Session> findByUserUserId(Integer userId);
}