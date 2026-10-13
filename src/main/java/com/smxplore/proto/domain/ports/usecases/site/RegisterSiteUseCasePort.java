package com.smxplore.proto.domain.ports.usecases.site;

import com.smxplore.proto.domain.model.site.Site;

import reactor.core.publisher.Mono;

public interface RegisterSiteUseCasePort {

    Mono<Site> handle(Site site);
}
