package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;
import com.smxplore.proto.domain.ports.repository.ServiceProviderRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.ViewServiceProviderProfileUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ViewServiceProviderProfileUseCase implements ViewServiceProviderProfileUseCasePort {

    private final ServiceProviderRepositoryPort serviceProviderRepository;

    @Override
    public Mono<ServiceProvider> handle(UUID serviceProviderId) {
        return serviceProviderRepository.findById(serviceProviderId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(serviceProviderId)));
    }
}
