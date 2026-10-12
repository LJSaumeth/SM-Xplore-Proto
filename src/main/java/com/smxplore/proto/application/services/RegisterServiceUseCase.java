package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.exceptions.service.InvalidServiceProviderException;
import com.smxplore.proto.domain.exceptions.service.InvalidServiceRequestException;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.types.ProviderRef;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.RegisterServiceUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterServiceUseCase implements RegisterServiceUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Mono<Service> handle(ProviderRef provider, Service service) {
        return Mono.justOrEmpty(service)
                .switchIfEmpty(Mono.error(new InvalidServiceRequestException("Service cannot be null")))
                .map(s -> validateAndBuild(s, provider))
                .flatMap(serviceRepo::save);
    }

    private Service validateAndBuild(Service service, ProviderRef provider) {
        if (provider == null) throw new InvalidServiceProviderException("Provider cannot be null");
        var serviceWithProvider = service.toBuilder().provider(provider).build();
        serviceWithProvider.validate();
        return serviceWithProvider;
    }
}
