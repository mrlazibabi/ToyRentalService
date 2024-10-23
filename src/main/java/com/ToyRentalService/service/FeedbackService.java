package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.FeedbackRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Feedback;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.AccountRepository;
import com.ToyRentalService.repository.FeedbackRepository;
import com.ToyRentalService.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    AuthenticationService authenticationService;

    public Feedback createNewFeedback(FeedbackRequest feedbackRequest){
        Post post = postRepository.findById(feedbackRequest.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Shop Not Found"));
        Feedback feedback = new Feedback();
        feedback.setContent(feedbackRequest.getContent());
        feedback.setRating(feedbackRequest.getRating());
        feedback.setCustomer(authenticationService.getCurrentAccount());
        feedback.setPost(post);

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedback(long postId) {
        Optional<Post> postOptional = postService.getPostById(postId);

        // Check if the post is found
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            return feedbackRepository.findFeedbackByPostId(post.getId());
        } else {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }
    }
}
