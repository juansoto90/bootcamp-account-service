package com.nttdata.account.service.impl;

import com.nttdata.account.model.entity.Account;
import com.nttdata.account.model.entity.Customer;
import com.nttdata.account.repository.IAccountRepository;
import com.nttdata.account.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private final IAccountRepository repository;

    @Override
    public Mono<Account> save(Account account) {
        return repository.save(account);
    }

    @Override
    public Mono<Account> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Account> findAllByCustomerOwnerIn(List<Customer> customers) {
        return repository.findAllByCustomerOwnerIn(customers);
    }

    @Override
    public Flux<Account> findByCustomerDocumentNumber(String documentNumber) {
        return repository.findByCustomerDocumentNumber(documentNumber);
    }

}
