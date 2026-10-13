package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.zone.Zone;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Persistence contract for {@link Zone} (PostgreSQL). Implemented by an outbound adapter.
 */
public interface IZoneRepository {

    /** Persists the zone (create or update) and returns the stored state. */
    Mono<Zone> save(Zone zone);

    Mono<Zone> findById(UUID id);

    Mono<Boolean> existsById(UUID id);

    Flux<Zone> findAll();
}
