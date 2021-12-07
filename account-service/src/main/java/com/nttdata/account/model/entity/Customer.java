package com.nttdata.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    //@Id
    private String id;
    private String customerIdNumber;
    private String customerType;
    private String documentType;
    private String documentNumber;
    private String name;
    private String legalRepresentative;
    private String email;
    private String phone;
    private String address;
}
