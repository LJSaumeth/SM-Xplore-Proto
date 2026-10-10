package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByTypeUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class FindServicesByTypeUseCase implements FindServicesByTypeUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Flux<Service> handle(ServiceType type, ServiceStatus status) {
        return serviceRepo.findAllByStatusAndType(status, type);
    }
}
