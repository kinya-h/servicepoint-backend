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
     * Handle payment success callback
     */
    @GetMapping("/success")
    public ResponseEntity<?> handlePaymentSuccess(
            @RequestParam("session_id") String sessionId,
            @RequestParam("booking_id") Integer bookingId
    ) {
        try {
            paymentService.handlePaymentSuccess(sessionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment successful");
            response.put("bookingId", bookingId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process payment success");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Handle payment cancellation
     */
    @GetMapping("/cancel")
    public ResponseEntity<?> handlePaymentCancellation(@RequestParam("booking_id") Integer bookingId) {
        try {
            paymentService.handlePaymentCancellation(bookingId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment cancelled");
            response.put("bookingId", bookingId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process payment cancellation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Stripe webhook endpoint
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
                    paymentService.handlePaymentSuccess(session.getId());
                    break;

                case "payment_intent.succeeded":
                    // Additional handling if needed
                    break;

                case "payment_intent.payment_failed":
                    // Handle failed payment
                    break;

                default:
                    // Unhandled event type
                    break;
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook error: " + e.getMessage());
        }
    }
}