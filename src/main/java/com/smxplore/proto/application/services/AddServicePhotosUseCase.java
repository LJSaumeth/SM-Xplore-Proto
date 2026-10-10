package com.smxplore.proto.application.services;

import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.service.AddServicePhotosUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class AddServicePhotosUseCase implements AddServicePhotosUseCasePort {
    private final ServiceRepositoryPort serviceRepo;

    @Override
    public Mono<Service> handle(UUID serviceId, Collection<String> photos) {
        return serviceRepo.findById(serviceId)
                .switchIfEmpty(Mono.error(new ServiceNotFoundException(serviceId)))
                .doOnNext(service -> {
                    service.addPhotos(photos);
                })
                .flatMap(serviceRepo::save);
    }
}
