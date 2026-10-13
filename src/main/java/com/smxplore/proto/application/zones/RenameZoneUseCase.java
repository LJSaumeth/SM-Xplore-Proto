package com.smxplore.proto.application.zones;

import com.smxplore.proto.domain.exceptions.ZoneNotFoundException;
import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;
import com.smxplore.proto.domain.ports.usecases.zone.RenameZoneUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class RenameZoneUseCase implements RenameZoneUseCasePort {

    private final IZoneRepository zoneRepository;

    @Override
    public Mono<Zone> handle(UUID zoneId, String newName) {
        return zoneRepository.findById(zoneId)
                .switchIfEmpty(Mono.error(new ZoneNotFoundException(zoneId)))
                .doOnNext(zone -> zone.rename(newName))
                .flatMap(zoneRepository::save);
    }
}
