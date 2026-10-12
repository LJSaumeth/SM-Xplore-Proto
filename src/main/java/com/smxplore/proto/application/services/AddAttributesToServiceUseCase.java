package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.types.Attribute;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.AddAttributeToServiceUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class AddAttributesToServiceUseCase implements AddAttributeToServiceUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Mono<Service> handle(UUID serviceId, Collection<Attribute> attributes) {
        return serviceRepo.findById(serviceId)
                .switchIfEmpty(Mono.error(new ServiceNotFoundException(serviceId)))
                .doOnNext(service -> {
                    service.addExtras(attributes);
                })
                .flatMap(serviceRepo::save);
    }
}
