package com.ToyRentalService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    @Autowired
    private OrderRentService orderRentService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndSetOverdueOrders() {
        orderRentService.updateOrderStatusToOverdue();
    }
}
