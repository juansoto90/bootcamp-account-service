package com.nttdata.account.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class BankAccountDto {
    private double balance;
    private String accountType;
    private String documentNumber;
    private List<String> customerOwner;
    private List<String> customerAuthorizedSigner;
}
