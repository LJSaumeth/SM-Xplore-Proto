package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.site.Site;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Persistence contract for {@link Site} (MongoDB). Implemented by an outbound adapter.
 */
public interface ISiteRepository {

    /** Persists the site (create or update) and returns the stored state. */
    Mono<Site> save(Site site);

    Mono<Site> findById(UUID id);

    Mono<Boolean> existsById(UUID id);

    Flux<Site> findAll();

    Flux<Site> findAllByZoneId(UUID zoneId);
}
