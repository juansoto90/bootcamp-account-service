package com.nttdata.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("account")
public class Account {
    @Id
    private String id;
    private double balance;
    private String accountNumber;
    private boolean maintenanceCommission;
    private boolean maximumMovementLimit;
    private Integer movementAmount;
    private Customer customer;
    private List<Customer> customerOwner;
    private List<Customer> customerAuthorizedSigner;
    private String status;

    private Acquisition acquisition;
}
