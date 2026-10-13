package com.smxplore.proto.domain.ports.usecases.zone;

import com.smxplore.proto.domain.model.zone.Zone;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RenameZoneUseCasePort {

    Mono<Zone> handle(UUID zoneId, String newName);
}
