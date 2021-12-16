package com.nttdata.account.model.dto;

import lombok.Data;

@Data
public class CreditCardAccountDto {
    private String accountType;
    private String documentNumber;
    private double creditLine;
}
