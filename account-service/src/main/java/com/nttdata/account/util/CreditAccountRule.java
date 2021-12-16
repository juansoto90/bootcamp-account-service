package com.nttdata.account.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditAccountRule {
    private String customerType;
    private Integer creditAmount;
    public CreditAccountRule(String customerType){
        this.customerType = customerType;
        this.creditAmount = customerType.equals("PERSONAL") ? 1 : Integer.MAX_VALUE;
    }
}
