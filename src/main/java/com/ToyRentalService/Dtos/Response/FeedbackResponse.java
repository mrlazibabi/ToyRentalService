package com.ToyRentalService.Dtos.Response;

import lombok.Data;

@Data
public class FeedbackResponse {
    private long id;
    private String content;
    private int rating;
    private String email;
}
