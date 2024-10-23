package com.ToyRentalService.Dtos.Request;

import lombok.Data;

@Data
public class FeedbackRequest {
    private String content;
    private int rating;
    private long postId;
}
