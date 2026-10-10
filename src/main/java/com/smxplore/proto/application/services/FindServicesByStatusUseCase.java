package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByStatusUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class FindServicesByStatusUseCase implements FindServicesByStatusUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Flux<Service> handle(ServiceStatus status) {
        return serviceRepo.findAllByStatus(status);
    }
}
