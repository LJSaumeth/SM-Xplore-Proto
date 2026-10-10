package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByProviderUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class FindServicesByProviderUseCase implements FindServicesByProviderUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Flux<Service> handle(UUID providerId) {
        //Buscar si el provider existe
        return serviceRepo.findAllByProviderId(providerId);
    }
}
