package com.nttdata.account.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditCard {
    private String id;
    private String cardNumber;
    private String cardType;
    private int expirationMonth;
    private int expirationYear;
    private String cvv;
    private Customer customer;
    private String status;
}
