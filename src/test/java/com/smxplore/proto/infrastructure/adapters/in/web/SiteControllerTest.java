package com.smxplore.proto.infrastructure.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.smxplore.proto.domain.exceptions.ZoneNotFoundException;
import com.smxplore.proto.domain.model.site.Site;
import com.smxplore.proto.domain.ports.usecases.site.FindSiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.ListSitesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.RegisterSiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.UpdateSiteUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.controller.SiteController;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SiteResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.error.GlobalExceptionHandler;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.SiteWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@WebFluxTest(controllers = SiteController.class)
@Import({SiteWebMapper.class, GlobalExceptionHandler.class})
class SiteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterSiteUseCasePort registerSiteUseCase;

    @MockitoBean
    private UpdateSiteUseCasePort updateSiteUseCase;

    @MockitoBean
    private FindSiteUseCasePort findSiteUseCase;

    @MockitoBean
    private ListSitesUseCasePort listSitesUseCase;

    @Autowired
    void configureClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser("tester"))
                .mutateWith(SecurityMockServerConfigurers.csrf());
    }

    @Test
    void shouldRegisterASite() {
        var zoneId = UUID.randomUUID();
        var site = new Site(UUID.randomUUID(), zoneId, "Tayrona", 11.31, -74.06, List.of());
        given(registerSiteUseCase.handle(any())).willReturn(Mono.just(site));

        var response = webTestClient.post().uri("/api/v1/sites")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"zoneId": "%s", "name": "Tayrona", "latitude": 11.31, "longitude": -74.06}
                        """.formatted(zoneId))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SiteResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.zoneId()).isEqualTo(zoneId);
    }

    @Test
    void shouldReturnNotFoundWhenTheZoneDoesNotExist() {
        var zoneId = UUID.randomUUID();
        given(registerSiteUseCase.handle(any())).willReturn(Mono.error(new ZoneNotFoundException(zoneId)));

        webTestClient.post().uri("/api/v1/sites")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"zoneId": "%s", "name": "Tayrona", "latitude": 11.31, "longitude": -74.06}
                        """.formatted(zoneId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldGetOneSite() {
        var site = new Site(UUID.randomUUID(), UUID.randomUUID(), "Tayrona", 11.31, -74.06, List.of());
        given(findSiteUseCase.handle(site.getId())).willReturn(Mono.just(site));

        webTestClient.get().uri("/api/v1/sites/{siteId}", site.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldListSitesFilteredByZone() {
        var zoneId = UUID.randomUUID();
        given(listSitesUseCase.handle(zoneId))
                .willReturn(Flux.just(new Site(UUID.randomUUID(), zoneId, "Tayrona", 11.31, -74.06, List.of())));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/sites")
                        .queryParam("zoneId", zoneId)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldUpdateASite() {
        var site = new Site(UUID.randomUUID(), UUID.randomUUID(), "Renamed", 11.31, -74.06, List.of());
        given(updateSiteUseCase.handle(site.getId(), "Renamed", null, null, null, null))
                .willReturn(Mono.just(site));

        var response = webTestClient.patch().uri("/api/v1/sites/{siteId}", site.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name": "Renamed"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SiteResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Renamed");
    }
}
