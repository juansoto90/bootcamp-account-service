package com.nttdata.account.service.impl;

import com.nttdata.account.model.entity.Association;
import com.nttdata.account.service.IAssociationService;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AssociationServiceImpl implements IAssociationService {

    private final WebClient.Builder webClientBuilder;
    private final String WEB_CLIENT_URL = "microservice.web.association";
    private final String BASE;

    public AssociationServiceImpl(WebClient.Builder webClientBuilder, Environment env) {
        this.webClientBuilder = webClientBuilder;
        BASE = env.getProperty(WEB_CLIENT_URL);
    }

    @Override
    public Mono<Association> save(Association association) {
        return webClientBuilder
                .baseUrl(BASE)
                .build()
                .post()
                .uri("/credit-card")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(association)
                .retrieve()
                .bodyToMono(Association.class);
    }
}
