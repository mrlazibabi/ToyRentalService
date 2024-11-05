package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.NotificationFCM;
import com.ToyRentalService.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name="api")
@RestController("/api/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @PostMapping("notificationFCM")
    public void sendNotification(@RequestBody NotificationFCM notificationFCM){
        notificationService.sendNotification(notificationFCM);
    }
}
