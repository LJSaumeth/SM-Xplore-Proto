package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.site.Site;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for {@link Site}. Implemented by an outbound adapter.
 */
public interface ISiteRepository {

    /** Persists the site (create or update) and returns the stored state. */
    Site save(Site site);

    Optional<Site> findById(UUID id);

    boolean existsById(UUID id);
}
