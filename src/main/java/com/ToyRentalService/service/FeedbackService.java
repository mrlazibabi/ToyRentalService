package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.FeedbackRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Feedback;
import com.ToyRentalService.entity.Toy;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.AccountRepository;
import com.ToyRentalService.repository.FeedbackRepository;
import com.ToyRentalService.repository.ToyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class  FeedbackService {
    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    ToyRepository toyRepository;

    @Autowired
    ToyService toyService;

    @Autowired
    AuthenticationService authenticationService;

    public Feedback createNewFeedback(FeedbackRequest feedbackRequest){
        Account customer = authenticationService.getCurrentAccount();
        Toy toy = toyRepository.findById(feedbackRequest.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Shop Not Found"));

        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setFromUser(customer.getUsername());
        feedback.setContent(feedbackRequest.getContent());
        feedback.setRating(feedbackRequest.getRating());
        feedback.setCustomer(authenticationService.getCurrentAccount());
        feedback.setToy(toy);

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedback(long toyId) {
        Optional<Toy> toyOptional = toyService.getToyById(toyId);

        // Check if the toy is found
        if (toyOptional.isPresent()) {
            Toy toy = toyOptional.get();
            return feedbackRepository.findFeedbackByToyId(toy.getId());
        } else {
            throw new EntityNotFoundException("Toy not found with id: " + toyId);
        }
    }
}
