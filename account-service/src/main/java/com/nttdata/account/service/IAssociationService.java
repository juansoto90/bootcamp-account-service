package com.nttdata.account.service;

import com.nttdata.account.model.entity.Association;
import reactor.core.publisher.Mono;

public interface IAssociationService {
    public Mono<Association> save(Association association);
}
