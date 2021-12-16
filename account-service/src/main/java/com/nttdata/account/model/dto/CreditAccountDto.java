package com.nttdata.account.model.dto;

import lombok.Data;

@Data
public class CreditAccountDto {
    private String accountType;
    private String documentNumber;
    private double amount;
}
