package com.nttdata.account.service;

import com.nttdata.account.model.entity.Account;
import com.nttdata.account.model.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IAccountService {
    public Mono<Account> save(Account account);
    public Mono<Account> findById(String id);
    public Flux<Account> findAllByCustomerOwnerIn(List<Customer> customers);
    public Flux<Account> findByCustomerDocumentNumber(String documentNumber);
}
