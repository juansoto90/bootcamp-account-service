package com.nttdata.account.service.impl;

import com.nttdata.account.model.entity.CreditCard;
import com.nttdata.account.service.ICreditCardService;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CreditCardServiceImpl implements ICreditCardService {

    private final WebClient.Builder webClientBuilder;
    private final String WEB_CLIENT_URL = "microservice.web.creditcard";
    private final String BASE;

    public CreditCardServiceImpl(WebClient.Builder webClientBuilder, Environment env) {
        this.webClientBuilder = webClientBuilder;
        BASE = env.getProperty(WEB_CLIENT_URL);
    }

    @Override
    public Mono<CreditCard> save(CreditCard creditCard) {
        return webClientBuilder
                .baseUrl(BASE)
                .build()
                .post()
                .uri("/credit")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(creditCard)
                .retrieve()
                .bodyToMono(CreditCard.class);
    }
}
