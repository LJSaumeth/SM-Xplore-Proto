package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApproveServiceProviderRegistrationUseCasePort {

    Mono<ServiceProvider> handle(UUID serviceProviderId);
}
