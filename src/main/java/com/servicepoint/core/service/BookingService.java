package com.servicepoint.core.service;

import com.servicepoint.core.dto.UpdateBookingRequest;
import com.servicepoint.core.dto.BookingRequest;
import com.servicepoint.core.dto.BookingInfo;
import com.servicepoint.core.dto.NewBookingResponse;
import com.servicepoint.core.model.Booking;

import java.util.List;

public interface BookingService {

    List<BookingInfo> findAllBookings();
    BookingInfo findBookingById(Integer bookingId);
    Booking updateBooking(Integer bookingId, UpdateBookingRequest request);
    NewBookingResponse saveBooking(BookingRequest booking);
    void deleteBooking(Integer bookingId);
    List<BookingInfo> findBookingsByCustomerId(Integer customerId);
    List<BookingInfo> findBookingsByProviderId(Integer providerId);
}
