package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.MarkServiceAsAvailableUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class MarkServiceAsAvailableUseCase implements MarkServiceAsAvailableUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Mono<Service> handle(UUID serviceId) {
        return serviceRepo.findById(serviceId)
                .switchIfEmpty(Mono.error(new ServiceNotFoundException(serviceId)))
                .doOnNext(Service::markAvailable)
                .flatMap(serviceRepo::save);
    }
}
