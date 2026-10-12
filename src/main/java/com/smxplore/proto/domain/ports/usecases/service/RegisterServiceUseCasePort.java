package com.smxplore.proto.domain.ports.usecases.service;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.types.ProviderRef;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface RegisterServiceUseCasePort {
    Mono<Service> handle(ProviderRef provider, Service service);
}
