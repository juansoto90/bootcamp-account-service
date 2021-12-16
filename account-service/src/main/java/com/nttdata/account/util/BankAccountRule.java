package com.nttdata.account.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankAccountRule {
    private String accountType;
    private String customerType;
    private boolean maintenanceCommission;
    private boolean maximumMovementLimit;
    private Integer movementAmount;
    private Integer maximumAccount;
    private Integer minimumHeadlines;
    private Integer maximumHeadlines;
    private Integer minimumAuthorizedSigners;
    private Integer maximumAuthorizedSigners;

    public BankAccountRule(String accountType, String customerType){
        //accountType: SAVING_ACCOUNT, CURRENT_ACCOUNT AND FIXED_TERM_ACCOUNT
        //customerType: PERSONAL AND ENTERPRISE
        this.accountType = accountType;
        this.customerType = customerType;
        this.maintenanceCommission = accountType.equals("SAVING_ACCOUNT") ? false : accountType.equals("CURRENT_ACCOUNT") ? true : false;
        this.maximumMovementLimit = accountType.equals("SAVING_ACCOUNT") ? true : accountType.equals("CURRENT_ACCOUNT") ? false : true;
        this.movementAmount = accountType.equals("SAVING_ACCOUNT")  ? 50 : accountType.equals("CURRENT_ACCOUNT") ? Integer.MAX_VALUE : 1;
        this.maximumAccount = accountType.equals("SAVING_ACCOUNT")  ? customerType.equals("PERSONAL") ? 1 : 0 :
                              accountType.equals("CURRENT_ACCOUNT") ? customerType.equals("PERSONAL") ? 1 : Integer.MAX_VALUE :
                                                                      customerType.equals("PERSONAL") ? 1 : 0;
        this.minimumHeadlines = customerType.equals("PERSONAL") ? 1 : 1;
        this.maximumHeadlines = customerType.equals("PERSONAL") ? 1 : Integer.MAX_VALUE;
        this.minimumAuthorizedSigners = customerType.equals("PERSONAL") ? 0 : 0;
        this.maximumAuthorizedSigners = customerType.equals("PERSONAL") ? 0 : Integer.MAX_VALUE;
    }
}
