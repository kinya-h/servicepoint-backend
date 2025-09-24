package com.servicepoint.core.controller;

import com.servicepoint.core.dto.*;
import com.servicepoint.core.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingInfo>> getAllBookings(
            @RequestParam(required = false) Integer customer_id,
            @RequestParam(required = false) Integer provider_id) {

        // If customer_id is provided, return bookings for that customer
        if (customer_id != null) {
            return ResponseEntity.ok(bookingService.findBookingsByCustomerId(customer_id));
        }

        // If provider_id is provided, return bookings for that provider
        if (provider_id != null) {
            return ResponseEntity.ok(bookingService.findBookingsByProviderId(provider_id));
        }

        // If no query parameters, return all bookings
        return ResponseEntity.ok(bookingService.findAllBookings());
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingInfo> getBookingById(@PathVariable Integer bookingId) {
         return ResponseEntity.ok(bookingService.findBookingById(bookingId));
    }

    @PostMapping
        public ResponseEntity<NewBookingResponse> createBooking(@RequestBody BookingRequest request) {

        NewBookingResponse createdBooking = bookingService.saveBooking(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBooking.bookingId() )
                .toUri();
        return ResponseEntity.created(location).body(createdBooking);
    }

        @PutMapping("/{bookingId}")
        public ResponseEntity<UpdateBookingResponse> updateBooking(@PathVariable Integer bookingId, @RequestBody UpdateBookingRequest updateBookingRequest) {

            updateBookingRequest.setBookingId(bookingId); //set the id since the expected data does not have id since it is already passed in the url
            var updatedBooking = bookingService.updateBooking(bookingId, updateBookingRequest);
            return  ResponseEntity.ok(new UpdateBookingResponse(
                    updatedBooking.getBookingId(),
                    updatedBooking.getServiceDateTime(),
                    updatedBooking.getStatus(),
                    updatedBooking.getNotes(),
                    updatedBooking.getPriceAtBooking(),
                    updatedBooking.getPricingTypeAtBooking(),
                    new CustomerInfo(
                            updatedBooking.getCustomer().getUserId(),

                            updatedBooking.getCustomer().getUsername(),
                            updatedBooking.getCustomer().getEmail()
                    ),
                    new ProviderInfo(
                            updatedBooking.getProvider().getUserId(),

                            updatedBooking.getProvider().getUsername(),
                            updatedBooking.getProvider().getEmail(),
                            updatedBooking.getProvider().getRole()
                    )
            ));

    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Integer bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}