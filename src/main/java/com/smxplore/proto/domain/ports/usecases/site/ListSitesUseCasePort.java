package com.smxplore.proto.domain.ports.usecases.site;

import com.smxplore.proto.domain.model.site.Site;

import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ListSitesUseCasePort {

    /** Lists every site when {@code zoneId} is null, or the sites of that zone otherwise. */
    Flux<Site> handle(UUID zoneId);
}
