package com.smxplore.proto.domain.ports.usecases.site;

import com.smxplore.proto.domain.model.site.Site;

import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

public interface UpdateSiteUseCasePort {

    Mono<Site> handle(UUID siteId, String name, Double latitude, Double longitude, UUID zoneId,
                      Collection<String> photoUrls);
}
