package com.ToyRentalService.Dtos.Request;

import com.ToyRentalService.entity.Account;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class EmailDetail {
    private Account receiver;
    private String subject;
    private String link;
}
