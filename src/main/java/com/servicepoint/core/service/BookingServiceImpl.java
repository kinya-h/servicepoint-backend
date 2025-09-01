package com.servicepoint.core.service;

import com.servicepoint.core.controller.UpdateBookingRequest;
import com.servicepoint.core.dto.*;
import com.servicepoint.core.exception.ResourceNotFoundException;
import com.servicepoint.core.model.Booking;
import com.servicepoint.core.model.ServiceCatalog;
import com.servicepoint.core.model.User;
import com.servicepoint.core.repository.BookingRepository;
import com.servicepoint.core.repository.ServiceCatalogRepository;
import com.servicepoint.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceCatalogRepository serviceCatalogRepository;
    @Override
    public List<BookingInfo> findAllBookings() {
        var bookings =  bookingRepository.findAll();

        return bookings.stream().map(booking -> new BookingInfo(
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getServiceDateTime(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getPriceAtBooking(),
                booking.getPricingTypeAtBooking(),
                new CustomerInfo(
                        booking.getCustomer().getUserId(),
                        booking.getCustomer().getUsername(),
                        booking.getCustomer().getEmail()
                ),
                new ProviderInfo(
                        booking.getProvider().getUserId(),
                        booking.getProvider().getUsername(),
                        booking.getProvider().getEmail(),
                        booking.getProvider().getRole()
                ),
                new ServiceInfo(
                        booking.getService().getServiceId(),
                        booking.getService().getName(),
                        booking.getService().getDescription(),
                        booking.getService().getCategory(),
                        booking.getService().getAvailability(),
                        booking.getService().getPrice(),
                        booking.getService().getPricingType(),
                        booking.getService().getLevel(),
                        booking.getService().getSubject()
                )
        )).collect(Collectors.toList());
    }

    @Override
    public BookingInfo findBookingById(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        return new BookingInfo(
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getServiceDateTime(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getPriceAtBooking(),
                booking.getPricingTypeAtBooking(),
                new CustomerInfo(
                        booking.getCustomer().getUserId(),
                        booking.getCustomer().getUsername(),
                        booking.getCustomer().getEmail()
                ),
                new ProviderInfo(
                        booking.getProvider().getUserId(),
                        booking.getProvider().getUsername(),
                        booking.getProvider().getEmail(),
                        booking.getProvider().getRole()
                ),
                new ServiceInfo(
                        booking.getService().getServiceId(), booking.getService().getName(),
                        booking.getService().getDescription(),
                        booking.getService().getCategory(),
                        booking.getService().getAvailability(),
                        booking.getService().getPrice(),
                        booking.getService().getPricingType(),
                         booking.getService().getLevel(), booking.getService().getSubject()

                )
        );
    }


    @Override
    public Booking updateBooking(Integer bookingId, UpdateBookingRequest request) {
        Booking existing = bookingRepository.findById(bookingId).
                orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

        existing.setServiceDateTime(request.getServiceDateTime());
        existing.setStatus(request.getStatus());
        existing.setNotes(request.getNotes());
        existing.setPriceAtBooking(request.getPriceAtBooking());
        existing.setPricingTypeAtBooking(request.getPricingTypeAtBooking());

        return bookingRepository.save(existing);
    }

    @Override
    public NewBookingResponse saveBooking(BookingRequest request) {
        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        ServiceCatalog service = serviceCatalogRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Does not exist"));

        var booking = new Booking();
        booking.setCustomer(customer);
        booking.setProvider(provider);
        booking.setService(service);
        booking.setBookingDate(request.getBookingDate());
        booking.setPriceAtBooking(request.getPriceAtBooking());
        booking.setPricingTypeAtBooking(request.getPricingTypeAtBooking());
        booking.setStatus(request.getStatus());
        booking.setServiceDateTime(request.getServiceDateTime());
        booking.setNotes(request.getNotes());

        bookingRepository.save(booking);

        // Build and return NewBookingResponse
        return new NewBookingResponse(
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getServiceDateTime(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getPriceAtBooking(),
                booking.getPricingTypeAtBooking(),
                new CustomerInfo(customer.getUserId(),customer.getUsername(), customer.getEmail()),
                new ProviderInfo(provider.getUserId(), provider.getUsername(), booking.getProvider().getEmail(), booking.getProvider().getRole()),
                new ServiceInfo(service.getServiceId(), service.getName(),
                        service.getDescription(), service.getCategory(),
                        service.getAvailability(), service.getPrice(), service.getPricingType(),
                        service.getLevel(), service.getSubject())
        );
    }


    @Override
    public void deleteBooking(Integer bookingId) {

        var booking = bookingRepository.findById(bookingId).
                orElseThrow(()-> new ResourceNotFoundException("Booking not found"));
        bookingRepository.deleteById(booking.getBookingId());
    }

    @Override
    public List<BookingInfo> findBookingsByCustomerId(Integer customerId) {
        var bookings =  bookingRepository.findByCustomerUserId(customerId);


        return bookings.stream().map(booking -> new BookingInfo(
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getServiceDateTime(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getPriceAtBooking(),
                booking.getPricingTypeAtBooking(),
                new CustomerInfo(
                        booking.getCustomer().getUserId(),
                        booking.getCustomer().getUsername(),
                        booking.getCustomer().getEmail()
                ),
                new ProviderInfo(
                        booking.getProvider().getUserId(),
                        booking.getProvider().getUsername(),
                        booking.getProvider().getEmail(),
                        booking.getProvider().getRole()
                ),
                new ServiceInfo(
                        booking.getService().getServiceId(),
                        booking.getService().getName(),
                        booking.getService().getDescription(),
                        booking.getService().getCategory(),
                        booking.getService().getAvailability(),
                        booking.getService().getPrice(),
                        booking.getService().getPricingType(),
                        booking.getService().getLevel(),
                        booking.getService().getSubject()
                )
        )).collect(Collectors.toList());

    }

    @Override
    public List<BookingInfo> findBookingsByProviderId(Integer providerId) {
        var bookings =  bookingRepository.findByProviderUserId(providerId);
        return bookings.stream().map(booking -> new BookingInfo(
                booking.getBookingId(),
                booking.getBookingDate(),
                booking.getServiceDateTime(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getPriceAtBooking(),
                booking.getPricingTypeAtBooking(),
                new CustomerInfo(
                        booking.getCustomer().getUserId(),
                        booking.getCustomer().getUsername(),
                        booking.getCustomer().getEmail()
                ),
                new ProviderInfo(
                        booking.getProvider().getUserId(),
                        booking.getProvider().getUsername(),
                        booking.getProvider().getEmail(),
                        booking.getProvider().getRole()
                ),
                new ServiceInfo(
                        booking.getService().getServiceId(),
                        booking.getService().getName(),
                        booking.getService().getDescription(),
                        booking.getService().getCategory(),
                        booking.getService().getAvailability(),
                        booking.getService().getPrice(),
                        booking.getService().getPricingType(),
                        booking.getService().getLevel(),
                        booking.getService().getSubject()
                )
        )).collect(Collectors.toList());
    }
}