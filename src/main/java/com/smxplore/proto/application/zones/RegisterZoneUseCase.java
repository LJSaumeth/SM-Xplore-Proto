package com.smxplore.proto.application.zones;

import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;
import com.smxplore.proto.domain.ports.usecases.zone.RegisterZoneUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class RegisterZoneUseCase implements RegisterZoneUseCasePort {

    private final IZoneRepository zoneRepository;

    @Override
    public Mono<Zone> handle(String name) {
        return zoneRepository.save(new Zone(UUID.randomUUID(), name));
    }
}
