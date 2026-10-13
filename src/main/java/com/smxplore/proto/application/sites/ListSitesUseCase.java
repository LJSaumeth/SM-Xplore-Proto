package com.smxplore.proto.application.sites;

import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.usecases.site.ListSitesUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class ListSitesUseCase implements ListSitesUseCasePort {

    private final ISiteRepository siteRepository;

    @Override
    public Flux<Site> handle(UUID zoneId) {
        return zoneId == null ? siteRepository.findAll() : siteRepository.findAllByZoneId(zoneId);
    }
}
