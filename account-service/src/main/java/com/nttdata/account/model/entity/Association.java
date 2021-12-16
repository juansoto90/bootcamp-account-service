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
public class Association {
    private String id;
    private String cardNumber;
    private String accountNumber;
    private String cardType;
    private String accountType;
    private String documentNumber;
    private boolean principal;
    private String status;
}
