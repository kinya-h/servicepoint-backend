package com.servicepoint.core.repository;

import com.servicepoint.core.model.HomeRepairCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeRepairCategoryRepository extends JpaRepository<HomeRepairCategory, Integer> {
}