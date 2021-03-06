package com.nttdata.account.service.impl;

import com.nttdata.account.model.entity.Customer;
import com.nttdata.account.service.ICustomerService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final WebClient.Builder webClientBuilder;
    private final String WEB_CLIENT_URL = "microservice.web.customer";
    private final String URI;

    public CustomerServiceImpl(WebClient.Builder webClientBuilder, Environment env) {
        this.webClientBuilder = webClientBuilder;
        URI = env.getProperty(WEB_CLIENT_URL);
    }

    @Override
    public Mono<Customer> findById(String id) {
        return webClientBuilder.build().get().uri(URI + "/{id}", id)
                .retrieve().bodyToMono(Customer.class);
    }

    @Override
    public Mono<Customer> findByDocumentNumber(String documentNumber) {
        return webClientBuilder.build().get().uri(URI + "/document-number/{documentNumber}", documentNumber)
                .retrieve().bodyToMono(Customer.class);
    }

}
