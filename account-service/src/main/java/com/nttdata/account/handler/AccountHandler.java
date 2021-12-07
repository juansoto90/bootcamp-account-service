package com.nttdata.account.handler;

import com.nttdata.account.model.entity.Account;
import com.nttdata.account.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccountHandler {

    @Autowired
    private final IAccountService service;

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<Account> accountMono = request.bodyToMono(Account.class);
        return  accountMono
                .flatMap(service::save)
                .flatMap(a -> ServerResponse.created(URI.create("/account/".concat(a.getId())))
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(a)
                )
                .onErrorResume(t -> {
                    return Mono.just(t).cast(WebExchangeBindException.class)
                            .flatMap(e -> Mono.just(e.getFieldErrors()))
                            .flatMapMany(Flux::fromIterable)
                            .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                            .collectList()
                            .flatMap(list -> {
                                Map<String, Object> body = new HashMap<>();
                                body.put("error", list);
                                body.put("status", HttpStatus.BAD_REQUEST.value());
                                return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(body);
                            });
                });

    }

    public Mono<ServerResponse> findAllByCustomerOwner(ServerRequest request){
        Mono<Account> accountMono = request.bodyToMono(Account.class);
        return  accountMono
                .flatMapMany(a -> {
                    return service.findAllByCustomerOwnerIn(a.getCustomerOwner());
                })
                .collectList()
                .flatMap(a -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a)
                );
    }

    public Mono<ServerResponse> findByCustomerDocumentNumber(ServerRequest request){
        String documentNumber = request.pathVariable("documentNumber");
        return service.findByCustomerDocumentNumber(documentNumber)
                .collectList()
                .flatMap(a -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a)
                );
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return service.findById(id)
                .flatMap(a -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a)
                );
    }
}
