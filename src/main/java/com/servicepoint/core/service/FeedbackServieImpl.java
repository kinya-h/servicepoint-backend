package com.servicepoint.core.service;

import com.servicepoint.core.dto.*;
import com.servicepoint.core.exception.ResourceNotFoundException;
import com.servicepoint.core.model.Feedback;
import com.servicepoint.core.model.ServiceCatalog;
import com.servicepoint.core.model.User;
import com.servicepoint.core.repository.BookingRepository;
import com.servicepoint.core.repository.FeedbackRepository;
import com.servicepoint.core.repository.ServiceCatalogRepository;
import com.servicepoint.core.repository.UserRepository;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServieImpl implements FeedbackService{

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;


    @Override
    public List<FeedbackResponse> fetchAllFeedback() {
        var allFeedback = feedbackRepository.findAll();
        return toFeedBackResponse(allFeedback);

    }


    @Override
    public FeedbackResponse findFeedbackById(Integer feedbackId) {
        var feedback = feedbackRepository.findById(feedbackId);

        return feedback.map(f->   new FeedbackResponse(
                    new SimpleBookingInfo(
                            f.getBooking().getBookingId(),
                            f.getBooking().getBookingDate(),
                            f.getBooking().getServiceDateTime(),
                            f.getBooking().getStatus(),
                            f.getBooking().getNotes(),
                            f.getBooking().getPriceAtBooking(),
                            f.getBooking().getPricingTypeAtBooking()
                    ),
                    new CustomerInfo(
                            f.getCustomer().getUserId(),
                            f.getCustomer().getUsername(),
                            f.getCustomer().getEmail()
                    ),
                    new ProviderInfo(

                            f.getProvider().getUserId(),
                            f.getProvider().getUsername(),
                            f.getProvider().getEmail(),
                            f.getProvider().getRole()
                    ),
                    f.getComments(),
                    f.getSubmissionDate()
            )).get();


    }

    @Override
    public FeedbackResponse saveFeedback(FeedbackRequest request) {
        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));


        var booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        var newFeedback = new Feedback();
        newFeedback.setBooking(booking);
        newFeedback.setCustomer(customer);
        newFeedback.setProvider(provider);
        newFeedback.setComments(request.getComments());
        newFeedback.setSubmissionDate(request.getSubmissionDate());

        var savedFeedback = feedbackRepository.save(newFeedback);
        return new FeedbackResponse(
                new SimpleBookingInfo(
                        savedFeedback.getBooking().getBookingId(),
                        savedFeedback.getBooking().getServiceDateTime(),
                        savedFeedback.getBooking().getBookingDate(),
                        savedFeedback.getBooking().getStatus(),
                        savedFeedback.getBooking().getNotes(),
                        savedFeedback.getBooking().getPriceAtBooking(),
                        savedFeedback.getBooking().getPricingTypeAtBooking()
                ),
                new CustomerInfo(
                        savedFeedback.getCustomer().getUserId(),
                        savedFeedback.getCustomer().getUsername(),
                        savedFeedback.getCustomer().getEmail()
                ),
                new ProviderInfo(

                        savedFeedback.getProvider().getUserId(),
                        savedFeedback.getProvider().getUsername(),
                        savedFeedback.getProvider().getEmail(),
                        savedFeedback.getProvider().getRole()
                ),
                savedFeedback.getComments(),
                savedFeedback.getSubmissionDate()
        );
    }

    @Override
    public void deleteFeedback(Integer feedbackId) {
    var feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

    feedbackRepository.delete(feedback);
    }

    private static List<FeedbackResponse> toFeedBackResponse(List<Feedback> allFeedback) {
        return allFeedback.stream().map(feedback -> new FeedbackResponse(
                new SimpleBookingInfo(
                        feedback.getBooking().getBookingId(),
                        feedback.getBooking().getBookingDate(),
                        feedback.getBooking().getServiceDateTime(),
                        feedback.getBooking().getStatus(),
                        feedback.getBooking().getNotes(),
                        feedback.getBooking().getPriceAtBooking(),
                        feedback.getBooking().getPricingTypeAtBooking()
                ),
                new CustomerInfo(
                        feedback.getCustomer().getUserId(),
                        feedback.getCustomer().getUsername(),
                        feedback.getCustomer().getEmail()
                ),
                new ProviderInfo(

                        feedback.getProvider().getUserId(),
                        feedback.getProvider().getUsername(),
                        feedback.getProvider().getEmail(),
                        feedback.getProvider().getRole()
                ),
                feedback.getComments(),
                feedback.getSubmissionDate()
        )).collect(Collectors.toList());
    }

}



