package com.nttdata.account.repository;

import com.nttdata.account.model.entity.Account;
import com.nttdata.account.model.entity.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IAccountRepository extends ReactiveMongoRepository<Account, String> {
    Flux<Account> findAllByCustomerOwnerIn(List<Customer> customers);
    Flux<Account> findByCustomerDocumentNumber(String documentNumber);
}
