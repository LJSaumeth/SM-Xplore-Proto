package com.smxplore.proto.application.sites;

import com.smxplore.proto.domain.exceptions.SiteNotFoundException;
import com.smxplore.proto.domain.exceptions.ZoneNotFoundException;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;
import com.smxplore.proto.domain.ports.usecases.site.UpdateSiteUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class UpdateSiteUseCase implements UpdateSiteUseCasePort {

    private final ISiteRepository siteRepository;
    private final IZoneRepository zoneRepository;

    @Override
    public Mono<Site> handle(UUID siteId, String name, Double latitude, Double longitude, UUID zoneId,
                             Collection<String> photoUrls) {
        return siteRepository.findById(siteId)
                .switchIfEmpty(Mono.error(new SiteNotFoundException(siteId)))
                .flatMap(site -> ensureZoneExists(zoneId).thenReturn(site))
                .doOnNext(site -> applyChanges(site, name, latitude, longitude, zoneId, photoUrls))
                .flatMap(siteRepository::save);
    }

    private Mono<Void> ensureZoneExists(UUID zoneId) {
        if (zoneId == null) {
            return Mono.empty();
        }
        return zoneRepository.existsById(zoneId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new ZoneNotFoundException(zoneId)))
                .then();
    }

    private static void applyChanges(Site site, String name, Double latitude, Double longitude, UUID zoneId,
                                     Collection<String> photoUrls) {
        if (name != null) {
            site.rename(name);
        }
        if (latitude != null || longitude != null) {
            site.relocate(latitude != null ? latitude : site.getLatitude(),
                    longitude != null ? longitude : site.getLongitude());
        }
        if (zoneId != null) {
            site.changeZone(zoneId);
        }
        if (photoUrls != null) {
            site.replacePhotoUrls(photoUrls);
        }
    }
}
