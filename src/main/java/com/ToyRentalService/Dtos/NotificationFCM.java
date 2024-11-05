package com.ToyRentalService.Dtos;

import lombok.Data;

@Data
public class NotificationFCM {
    private String title;
    private String message;
    private String fcmToken;
}
