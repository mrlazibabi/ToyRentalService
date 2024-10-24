package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.FeedbackRequest;
import com.ToyRentalService.entity.Feedback;
import com.ToyRentalService.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity createNewFeedBack(@RequestBody FeedbackRequest feedbackRequest){
        Feedback feedback = feedbackService.createNewFeedback(feedbackRequest);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Feedback>> getFeedbackByPostId(@PathVariable Long postId) {
        List<Feedback> feedbacks = feedbackService.getFeedback(postId);
        return ResponseEntity.ok(feedbacks);
    }
}
