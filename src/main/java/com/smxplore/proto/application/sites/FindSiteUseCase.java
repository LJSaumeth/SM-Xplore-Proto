package com.smxplore.proto.application.sites;

import com.smxplore.proto.domain.exceptions.SiteNotFoundException;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.usecases.site.FindSiteUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class FindSiteUseCase implements FindSiteUseCasePort {

    private final ISiteRepository siteRepository;

    @Override
    public Mono<Site> handle(UUID siteId) {
        return siteRepository.findById(siteId)
                .switchIfEmpty(Mono.error(new SiteNotFoundException(siteId)));
    }
}
