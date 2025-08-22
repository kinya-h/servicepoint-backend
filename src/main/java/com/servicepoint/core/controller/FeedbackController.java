package com.servicepoint.core.controller;

import com.servicepoint.core.dto.FeedbackRequest;
import com.servicepoint.core.dto.FeedbackResponse;
import com.servicepoint.core.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * Get all feedback
     * @return List of all feedback responses
     */
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
        List<FeedbackResponse> feedbackList = feedbackService.fetchAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Get feedback by ID
     * @param feedbackId The ID of the feedback to retrieve
     * @return FeedbackResponse or 404 if not found
     */
    @GetMapping("/{feedbackId}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Integer feedbackId) {
        FeedbackResponse feedback = feedbackService.findFeedbackById(feedbackId);
        return ResponseEntity.ok(feedback);
    }

    /**
     * Create new feedback
     * @param feedbackRequest The feedback data to create
     * @return Created feedback response
     */
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        FeedbackResponse createdFeedback = feedbackService.saveFeedback(feedbackRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
    }

    /**
     * Delete feedback by ID
     * @param feedbackId The ID of the feedback to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get feedback by customer ID
     * @param customerId The customer ID to filter feedback by
     * @return List of feedback for the specific customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByCustomer(@PathVariable Integer customerId) {
        // TODO: add method to the service interface and implementation
        // List<FeedbackResponse> customerFeedback = feedbackService.findFeedbackByCustomerId(customerId);
        // return ResponseEntity.ok(customerFeedback);

        // For now, returning all feedback
        List<FeedbackResponse> feedbackList = feedbackService.fetchAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Get feedback by provider ID
     * @param providerId The provider ID to filter feedback by
     * @return List of feedback for the specific provider
     */
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByProvider(@PathVariable Integer providerId) {
        // TODO: add a method to the service interface and implementation
        // List<FeedbackResponse> providerFeedback = feedbackService.findFeedbackByProviderId(providerId);
        // return ResponseEntity.ok(providerFeedback);

        // For now, returning all feedback
        List<FeedbackResponse> feedbackList = feedbackService.fetchAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Get feedback by booking ID
     * @param bookingId The booking ID to filter feedback by
     * @return List of feedback for the specific booking
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByBooking(@PathVariable Integer bookingId) {
        //TODO:: add the service method and implementation to fetch feedback by booking
        // List<FeedbackResponse> bookingFeedback = feedbackService.findFeedbackByBookingId(bookingId);
        // return ResponseEntity.ok(bookingFeedback);

        // For now, returning all feedback
        List<FeedbackResponse> feedbackList = feedbackService.fetchAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }
}