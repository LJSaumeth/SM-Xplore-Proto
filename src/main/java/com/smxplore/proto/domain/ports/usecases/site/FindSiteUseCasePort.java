package com.smxplore.proto.domain.ports.usecases.site;

import com.smxplore.proto.domain.model.site.Site;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FindSiteUseCasePort {

    Mono<Site> handle(UUID siteId);
}
