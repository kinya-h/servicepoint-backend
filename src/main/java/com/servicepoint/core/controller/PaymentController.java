package com.servicepoint.core.controller;

import com.servicepoint.core.model.Booking;
import com.servicepoint.core.model.ServiceCatalog;
import com.servicepoint.core.repository.BookingRepository;
import com.servicepoint.core.repository.ServiceRepository;
import com.servicepoint.core.service.StripePaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private StripePaymentService paymentService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * Create payment session for booking
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody Map<String, Integer> request) {
        try {
            Integer bookingId = request.get("bookingId");

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            ServiceCatalog service = booking.getService();

            Session session = paymentService.createCheckoutSession(booking, service);

            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create payment session");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Check payment status (for frontend polling)
     * This is called by the frontend to check if webhook has processed the payment
     */
    @GetMapping("/status/{bookingId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Integer bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("bookingId", bookingId);
            response.put("paymentStatus", booking.getPaymentStatus());
            response.put("status", booking.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get payment status");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Stripe webhook endpoint (SERVER-TO-SERVER)
     * This is called directly by Stripe, not by the frontend
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            // Verify webhook signature and construct event
            Event event = paymentService.constructWebhookEvent(payload, sigHeader);

            if (event == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }

            // Handle different event types
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElseThrow(() -> new IllegalArgumentException("Session not found"));

                    // This is where the actual payment processing happens
                    paymentService.handlePaymentSuccess(session.getId());
                    break;

                case "checkout.session.expired":
                    Session expiredSession = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElseThrow(() -> new IllegalArgumentException("Session not found"));

                    // Handle expired checkout session
                    paymentService.handleSessionExpired(expiredSession.getId());
                    break;

                case "payment_intent.succeeded":
                    // Additional handling if needed
                    break;

                case "payment_intent.payment_failed":
                    // Handle failed payment
                    break;

                default:
                    // Log unhandled event type
                    System.out.println("Unhandled event type: " + event.getType());
                    break;
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook error: " + e.getMessage());
        }
    }

    /**
     * DEPRECATED: This endpoint should not be used for payment completion
     * Payment completion should only happen via webhook
     * This endpoint is kept only for backward compatibility
     */
    @Deprecated
    @GetMapping("/success")
    public ResponseEntity<?> handlePaymentSuccess(
            @RequestParam("session_id") String sessionId,
            @RequestParam("booking_id") Integer bookingId
    ) {
        try {
            // Just return the booking status, don't process payment here
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking found");
            response.put("bookingId", bookingId);
            response.put("paymentStatus", booking.getPaymentStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get booking details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Handle payment cancellation page
     */
    @GetMapping("/cancel")
    public ResponseEntity<?> handlePaymentCancellation(@RequestParam("booking_id") Integer bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment cancelled");
            response.put("bookingId", bookingId);
            response.put("paymentStatus", booking.getPaymentStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process payment cancellation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Verify payment with Stripe and complete the booking
     * This is called by frontend after Stripe redirect
     */
    @PostMapping("/verify-and-complete")
    public ResponseEntity<?> verifyAndCompletePayment(@RequestBody Map<String, String> request) {
        try {
            String sessionId = request.get("sessionId");
            Integer bookingId = Integer.parseInt(request.get("bookingId"));

            // 1. Retrieve the session from Stripe to verify it's actually paid
            Session session = Session.retrieve(sessionId);

            if (!"complete".equals(session.getStatus()) ||
                    !"paid".equals(session.getPaymentStatus())) {
                throw new IllegalStateException("Payment not completed");
            }

            // 2. Get the booking
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            // 3. Check if already processed (idempotent)
            if ("COMPLETED".equals(booking.getPaymentStatus())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Payment already processed");
                response.put("bookingId", bookingId);
                return ResponseEntity.ok(response);
            }

            // 4. Update booking status
            booking.setPaymentStatus("COMPLETED");
            booking.setStatus("CONFIRMED");
            booking.setStripeSessionId(sessionId);
            booking.setStripePaymentIntentId(session.getPaymentIntent());
            booking.setPaidAt(new java.sql.Timestamp(System.currentTimeMillis()));
            bookingRepository.save(booking);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment verified and booking confirmed");
            response.put("bookingId", bookingId);

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to verify payment with Stripe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}