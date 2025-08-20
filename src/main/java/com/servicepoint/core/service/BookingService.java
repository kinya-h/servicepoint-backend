package com.servicepoint.core.service;

import com.servicepoint.core.controller.UpdateBookingRequest;
import com.servicepoint.core.dto.BookingRequest;
import com.servicepoint.core.dto.BookingResponse;
import com.servicepoint.core.dto.NewBookingResponse;
import com.servicepoint.core.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    List<BookingResponse> findAllBookings();
    BookingResponse findBookingById(Integer bookingId);
    Booking updateBooking(Integer bookingId, UpdateBookingRequest request);
    NewBookingResponse saveBooking(BookingRequest booking);
    void deleteBooking(Integer bookingId);
    List<BookingResponse> findBookingsByCustomerId(Integer customerId);
    List<BookingResponse> findBookingsByProviderId(Integer providerId);
}
