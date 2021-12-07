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
        return route(POST("/account"), handler::create)
                .andRoute(GET("/account/{id}"), handler::findById)
                .andRoute(POST("/account/customerowner"), handler::findAllByCustomerOwner)
                .andRoute(GET("/account/customer/{documentNumber}"), handler::findByCustomerDocumentNumber);
    }
}
