package com.servicepoint.core.repository;
import com.servicepoint.core.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByCustomerUserId(Integer customerId);
    List<Booking> findByProviderUserId(Integer providerId);
}
