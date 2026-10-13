package com.smxplore.proto.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration(proxyBeanMethods = false)
public class TransactionConfiguration {

    /**
     * Operador transaccional reactivo para envolver los casos de uso que
     * escriben en más de un repositorio. El {@link ReactiveTransactionManager}
     * de R2DBC lo autoconfigura Spring Boot.
     */
    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}
