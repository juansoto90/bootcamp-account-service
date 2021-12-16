package com.nttdata.account.config;

import com.nttdata.account.handler.AccountHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> routes(AccountHandler handler){
        return route(POST("/account/bank-account"), handler::bankAccountCreate)
                .andRoute(POST("/account/credit-account"), handler::creditAccountCreate)
                .andRoute(POST("/account/credit-card-account"), handler::creditCardAccountCreate)
                .andRoute(GET("/account/{id}"), handler::findById)
                .andRoute(POST("/account/customerowner"), handler::findAllByCustomerOwner)
                .andRoute(GET("/account/customer/{documentNumber}"), handler::findByCustomerDocumentNumber)
                .andRoute(GET("/account/account-number/{accountNumber}"), handler::findByAccountNumber)
                .andRoute(POST("/account/update-amount"), handler::updateAmountAccount)
                .andRoute(POST("/account/update-consumption"), handler::updateConsumptionAccount);
    }
}
