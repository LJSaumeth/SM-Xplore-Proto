package com.smxplore.proto.domain.ports.usecases.zone;

import com.smxplore.proto.domain.model.zone.Zone;

import reactor.core.publisher.Mono;

public interface RegisterZoneUseCasePort {

    Mono<Zone> handle(String name);
}
