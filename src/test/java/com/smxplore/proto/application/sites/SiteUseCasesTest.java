package com.smxplore.proto.application.sites;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.SiteNotFoundException;
import com.smxplore.proto.domain.exceptions.ZoneNotFoundException;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.repository.ISiteRepository;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

class SiteUseCasesTest {

    private final ISiteRepository siteRepository = mock(ISiteRepository.class);
    private final IZoneRepository zoneRepository = mock(IZoneRepository.class);

    private final RegisterSiteUseCase registerSiteUseCase = new RegisterSiteUseCase(siteRepository, zoneRepository);
    private final UpdateSiteUseCase updateSiteUseCase = new UpdateSiteUseCase(siteRepository, zoneRepository);
    private final FindSiteUseCase findSiteUseCase = new FindSiteUseCase(siteRepository);
    private final ListSitesUseCase listSitesUseCase = new ListSitesUseCase(siteRepository);

    private Site site(UUID zoneId) {
        return new Site(UUID.randomUUID(), zoneId, "Tayrona", 11.31, -74.06, List.of());
    }

    @Test
    void shouldRegisterASiteWhenTheZoneExists() {
        var site = site(UUID.randomUUID());
        when(zoneRepository.existsById(site.getZoneId())).thenReturn(Mono.just(true));
        when(siteRepository.save(site)).thenReturn(Mono.just(site));

        StepVerifier.create(registerSiteUseCase.handle(site))
                .assertNext(saved -> assertThat(saved.getId()).isEqualTo(site.getId()))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenTheZoneOfTheSiteDoesNotExist() {
        var site = site(UUID.randomUUID());
        when(zoneRepository.existsById(site.getZoneId())).thenReturn(Mono.just(false));

        StepVerifier.create(registerSiteUseCase.handle(site))
                .expectError(ZoneNotFoundException.class)
                .verify();

        verify(siteRepository, never()).save(any());
    }

    @Test
    void shouldApplyOnlyThePresentFieldsOnUpdate() {
        var site = site(UUID.randomUUID());
        when(siteRepository.findById(site.getId())).thenReturn(Mono.just(site));
        when(siteRepository.save(site)).thenReturn(Mono.just(site));

        StepVerifier.create(updateSiteUseCase.handle(site.getId(), "Renamed", null, null, null, null))
                .assertNext(updated -> {
                    assertThat(updated.getName()).isEqualTo("Renamed");
                    assertThat(updated.getLatitude()).isEqualTo(11.31);
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUpdatingAMissingSite() {
        var id = UUID.randomUUID();
        when(siteRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(updateSiteUseCase.handle(id, "Renamed", null, null, null, null))
                .expectError(SiteNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenChangingToAMissingZone() {
        var site = site(UUID.randomUUID());
        var missingZoneId = UUID.randomUUID();
        when(siteRepository.findById(site.getId())).thenReturn(Mono.just(site));
        when(zoneRepository.existsById(missingZoneId)).thenReturn(Mono.just(false));

        StepVerifier.create(updateSiteUseCase.handle(site.getId(), null, null, null, missingZoneId, null))
                .expectError(ZoneNotFoundException.class)
                .verify();

        verify(siteRepository, never()).save(any());
    }

    @Test
    void shouldListAllSitesOrFilterByZone() {
        var zoneId = UUID.randomUUID();
        when(siteRepository.findAll()).thenReturn(Flux.just(site(zoneId)));
        when(siteRepository.findAllByZoneId(zoneId)).thenReturn(Flux.just(site(zoneId)));

        StepVerifier.create(listSitesUseCase.handle(null)).expectNextCount(1).verifyComplete();
        StepVerifier.create(listSitesUseCase.handle(zoneId)).expectNextCount(1).verifyComplete();
    }
}
