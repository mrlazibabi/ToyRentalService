package com.ToyRentalService.Dtos.Request;

import com.ToyRentalService.entity.Account;
import lombok.Data;

@Data
public class EmailDetail {
    private Account receiver;
    private String subject;
    private String link;
}
