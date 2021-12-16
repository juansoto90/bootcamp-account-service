package com.nttdata.account.handler;

import com.nttdata.account.exception.AccountException;
import com.nttdata.account.exception.messageException;
import com.nttdata.account.model.dto.BankAccountDto;
import com.nttdata.account.model.dto.CreditAccountDto;
import com.nttdata.account.model.dto.CreditCardAccountDto;
import com.nttdata.account.model.entity.Account;
import com.nttdata.account.model.entity.Association;
import com.nttdata.account.model.entity.CreditCard;
import com.nttdata.account.service.IAccountService;
import com.nttdata.account.service.IAssociationService;
import com.nttdata.account.service.ICreditCardService;
import com.nttdata.account.service.ICustomerService;
import com.nttdata.account.util.BankAccountRule;
import com.nttdata.account.util.CreditAccountRule;
import com.nttdata.account.util.CreditCardAccountRule;
import com.nttdata.account.util.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AccountHandler {

    private final IAccountService service;
    private final ICustomerService iCustomerService;
    private final ICreditCardService iCreditCardService;
    private final IAssociationService iAssociationService;

    public Mono<ServerResponse> bankAccountCreate(ServerRequest request){
        /*Mono<Account> accountMono = request.bodyToMono(Account.class);
        return  accountMono
                .map(a -> {
                    a.setAccountNumber(Generator.generateAccountNumber());
                    return  a;
                })
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
                });*/
        Mono<BankAccountDto> dto = request.bodyToMono(BankAccountDto.class);
        Account account = new Account();
        return dto
                .flatMap(d -> iCustomerService.findByDocumentNumber(d.getDocumentNumber())
                        .map(c -> {
                            account.setCustomer(c);
                            account.setBalance(d.getBalance());
                            account.setAccountType(d.getAccountType());
                            account.setAccountNumber(Generator.generateBankAccountNumber(d.getAccountType()));
                            return d;
                        })
                )
                .flatMap(d -> d.getCustomerOwner() == null ?
                        Mono.just(d) :
                        Flux.fromIterable(d.getCustomerOwner())
                                .flatMap(iCustomerService::findByDocumentNumber)
                                .collectList()
                                .map(list -> {
                                    account.setCustomerOwner(list);
                                    return d;
                                })
                )
                .flatMap(d -> d.getCustomerAuthorizedSigner() == null ?
                        Mono.just(d) :
                        Flux.fromIterable(d.getCustomerAuthorizedSigner())
                                .flatMap(iCustomerService::findByDocumentNumber)
                                .collectList()
                                .map(list -> {
                                    account.setCustomerAuthorizedSigner(list);
                                    return d;
                                })
                )
                .map(d -> account)
                .flatMap(a ->
                     service.findByCustomerDocumentNumber(a.getCustomer().getDocumentNumber())
                            .filter(f -> f.getStatus().equals("CREATED"))
                            .filter(f -> f.getAccountType().equals(a.getAccountType()))
                            .count()
                            .flatMap(c -> {
                                BankAccountRule accRule = new BankAccountRule(a.getAccountType(), a.getCustomer().getCustomerType());
                                if (accRule.getMaximumAccount() > Math.toIntExact(c)) {
                                    if (a.getCustomer().getCustomerType().equals("ENTERPRISE")){
                                        if (!(accRule.getMinimumHeadlines() <= account.getCustomerOwner().stream().count())){
                                            return Mono.error(
                                                    new WebClientResponseException(400,
                                                            messageException.maximumHeadlinesMessage(a.getCustomer().getCustomerType()),
                                                            null,null,null)
                                            );
                                        }
                                    } else {
                                        if (!(accRule.getMinimumHeadlines() <= account.getCustomerOwner().stream().count()
                                                && account.getCustomerOwner().stream().count() <= accRule.getMaximumHeadlines())){
                                            return Mono.error(
                                                    new WebClientResponseException(400,
                                                            messageException.maximumHeadlinesMessage(a.getCustomer().getCustomerType()),
                                                            null,null,null)
                                            );
                                        } else if (!(accRule.getMaximumAuthorizedSigners() >= Math.toIntExact(account.getCustomerAuthorizedSigner() == null ? 0 : account.getCustomerAuthorizedSigner().stream().count()))){
                                            return Mono.error(
                                                    new WebClientResponseException(400,
                                                            messageException.maximumAuthorizedSignersMessage(a.getCustomer().getCustomerType()),
                                                            null,null,null)
                                            );
                                        }
                                    }
                                    account.setMaintenanceCommission(accRule.isMaintenanceCommission());
                                    account.setMaximumMovementLimit(accRule.isMaximumMovementLimit());
                                    account.setMovementAmount(accRule.getMovementAmount());
                                    account.setStatus("CREATED");
                                    return service.save(account);
                                } else {
                                    return Mono.error(
                                            new WebClientResponseException(400,
                                                    messageException.accountQuantityValidationMessage(a.getAccountType(), a.getCustomer().getCustomerType()),
                                                    null,null,null)
                                    );
                                }
                            })
                )
                .flatMap(a -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a))
                .onErrorResume(AccountException::errorHandler);

    }

    public Mono<ServerResponse> creditCardAccountCreate(ServerRequest request){
        Mono<CreditCardAccountDto> dto = request.bodyToMono(CreditCardAccountDto.class);
        Account account = new Account();
        return dto
                .flatMap(d -> iCustomerService.findByDocumentNumber(d.getDocumentNumber())
                        .map(c -> {
                            account.setCustomer(c);
                            account.setAccountType(d.getAccountType());
                            account.setCreditLine(d.getCreditLine());
                            account.setAccountNumber(Generator.generateBankAccountNumber(d.getAccountType()));
                            account.setStatus("CREATED");
                            return d;
                        })
                )
                .map(d -> account)
                .flatMap(a ->
                         service.findByCustomerDocumentNumber(a.getCustomer().getDocumentNumber())
                            .filter(f -> f.getStatus().equals("CREATED"))
                            .filter(f -> f.getAccountType().equals(a.getAccountType()))
                            .count()
                            .flatMap(c -> {
                                CreditCardAccountRule creditCardRule = new CreditCardAccountRule(a.getCustomer().getCustomerType());
                                if (creditCardRule.getCreditCardAmount() <= Math.toIntExact(c)) {
                                    return Mono.error(
                                            new WebClientResponseException(400,
                                                    messageException.creditCardAmountMessage(),
                                                    null,null,null)
                                    );
                                }
                                return service.save(a)
                                        .flatMap(aq -> {
                                            CreditCard creditCard = new CreditCard();
                                            creditCard.setCustomer(a.getCustomer());
                                            creditCard.setCardType("CREDIT");
                                            creditCard.setStatus("CREATED");
                                            return iCreditCardService.save(creditCard)
                                                    .flatMap(cc -> {
                                                        Association association = new Association();
                                                        association.setAccountNumber(aq.getAccountNumber());
                                                        association.setCardNumber(cc.getCardNumber());
                                                        association.setCardType(cc.getCardType());
                                                        association.setAccountType(aq.getAccountType());
                                                        association.setDocumentNumber(aq.getCustomer().getDocumentNumber());
                                                        association.setPrincipal(true);
                                                        association.setStatus("ASSOCIATED");
                                                        return iAssociationService.save(association);
                                                    });
                                        });
                            })
                )
                .flatMap(a -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a))
                .onErrorResume(AccountException::errorHandler);
    }

    public Mono<ServerResponse> creditAccountCreate(ServerRequest request){
        Mono<CreditAccountDto> dto = request.bodyToMono(CreditAccountDto.class);
        Account account = new Account();
        return dto
                .flatMap(d -> iCustomerService.findByDocumentNumber(d.getDocumentNumber())
                        .map(c -> {
                            account.setCustomer(c);
                            account.setAmount(d.getAmount());
                            account.setAccountType(d.getAccountType());
                            account.setAccountNumber(Generator.generateBankAccountNumber(d.getAccountType()));
                            account.setStatus("CREATED");
                            return d;
                        })
                )
                .map(d -> account)
                .flatMap(a ->
                            service.findByCustomerDocumentNumber(a.getCustomer().getDocumentNumber())
                            .filter(f -> f.getStatus().equals("CREATED"))
                            .filter(f -> f.getAccountType().equals(a.getAccountType()))
                            .count()
                            .flatMap(c -> {
                                CreditAccountRule creditRule = new CreditAccountRule(a.getCustomer().getCustomerType());
                                if (creditRule.getCreditAmount() <= Math.toIntExact(c)) {
                                    return Mono.error(
                                            new WebClientResponseException(400,
                                                    messageException.creditAmountMessage(a.getCustomer().getCustomerType()),
                                                    null,null,null)
                                    );
                                }
                                return service.save(a);
                            })
                )
                .flatMap(a -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a))
                .onErrorResume(AccountException::errorHandler);
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

    public Mono<ServerResponse> findByAccountNumber(ServerRequest request){
        String accountNumber = request.pathVariable("accountNumber");
        return service.findByAccountNumber(accountNumber)
                .flatMap(a -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateAmountAccount(ServerRequest request){
        Mono<Account> accountMono = request.bodyToMono(Account.class);
        return accountMono
                .flatMap(a -> {
                    return service.findByAccountNumber(a.getAccountNumber())
                            .flatMap(ac -> {
                                ac.setBalance(a.getBalance());
                                return service.update(ac);
                            });
                })
                .flatMap(a -> ServerResponse.created(URI.create("/account/".concat(a.getId())))
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(a)
                );
    }

    public Mono<ServerResponse> updateConsumptionAccount(ServerRequest request){
        Mono<Account> accountMono = request.bodyToMono(Account.class);
        return accountMono
                .flatMap(a -> {
                    return service.findByAccountNumber(a.getAccountNumber())
                            .flatMap(ac -> {
                                ac.setConsumption(a.getConsumption());
                                return service.update(ac);
                            });
                })
                .flatMap(a -> ServerResponse.created(URI.create("/account/".concat(a.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(a)
                );
    }
}
