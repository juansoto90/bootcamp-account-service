package com.nttdata.account.service;

import com.nttdata.account.model.entity.Customer;
import reactor.core.publisher.Mono;

public interface ICustomerService {
    public Mono<Customer> findById(String id);
    public Mono<Customer> findByDocumentNumber(String documentNumber);
}
