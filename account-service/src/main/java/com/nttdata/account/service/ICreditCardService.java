package com.nttdata.account.service;

import com.nttdata.account.model.entity.CreditCard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICreditCardService {
    public Mono<CreditCard> save(CreditCard creditCard);
}
