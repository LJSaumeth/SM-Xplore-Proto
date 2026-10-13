package com.smxplore.proto.application.sites;

import com.smxplore.proto.domain.exceptions.ZoneNotFoundException;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;
import com.smxplore.proto.domain.ports.usecases.site.RegisterSiteUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterSiteUseCase implements RegisterSiteUseCasePort {

    private final ISiteRepository siteRepository;
    private final IZoneRepository zoneRepository;

    @Override
    public Mono<Site> handle(Site site) {
        return zoneRepository.existsById(site.getZoneId())
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new ZoneNotFoundException(site.getZoneId())))
                .then(Mono.defer(() -> siteRepository.save(site)));
    }
}
