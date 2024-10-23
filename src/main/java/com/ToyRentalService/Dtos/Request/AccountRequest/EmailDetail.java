package com.ToyRentalService.Dtos.Request.AccountRequest;

import com.ToyRentalService.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDetail {
    private Account receiver;
    private String subject;
    private String link;
}
