package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.zone.Zone;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for {@link Zone}. Implemented by an outbound adapter.
 */
public interface IZoneRepository {

    /** Persists the zone (create or update) and returns the stored state. */
    Zone save(Zone zone);

    Optional<Zone> findById(UUID id);

    boolean existsById(UUID id);
}
