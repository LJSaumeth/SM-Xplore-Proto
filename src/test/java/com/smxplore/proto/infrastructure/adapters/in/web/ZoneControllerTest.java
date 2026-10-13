package com.smxplore.proto.infrastructure.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.smxplore.proto.domain.exceptions.ZoneNotFoundException;
import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.domain.ports.usecases.zone.FindZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.ListZonesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.RegisterZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.RenameZoneUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.controller.ZoneController;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ZoneResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.error.GlobalExceptionHandler;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ZoneWebMapper;
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

import java.util.Objects;
import java.util.UUID;

@WebFluxTest(controllers = ZoneController.class)
@Import({ZoneWebMapper.class, GlobalExceptionHandler.class})
class ZoneControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterZoneUseCasePort registerZoneUseCase;

    @MockitoBean
    private RenameZoneUseCasePort renameZoneUseCase;

    @MockitoBean
    private FindZoneUseCasePort findZoneUseCase;

    @MockitoBean
    private ListZonesUseCasePort listZonesUseCase;

    @Autowired
    void configureClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser("tester"))
                .mutateWith(SecurityMockServerConfigurers.csrf());
    }

    @Test
    void shouldRegisterAZone() {
        var zone = new Zone(UUID.randomUUID(), "Historic Center");
        given(registerZoneUseCase.handle("Historic Center")).willReturn(Mono.just(zone));

        var response = webTestClient.post().uri("/api/v1/zones")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name": "Historic Center"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ZoneResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Historic Center");
    }

    @Test
    void shouldGetOneZone() {
        var zone = new Zone(UUID.randomUUID(), "Tayrona");
        given(findZoneUseCase.handle(zone.getId())).willReturn(Mono.just(zone));

        var response = webTestClient.get().uri("/api/v1/zones/{zoneId}", zone.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ZoneResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(zone.getId());
    }

    @Test
    void shouldReturnNotFoundWhenTheZoneDoesNotExist() {
        var id = UUID.randomUUID();
        given(findZoneUseCase.handle(id)).willReturn(Mono.error(new ZoneNotFoundException(id)));

        webTestClient.get().uri("/api/v1/zones/{zoneId}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldListZones() {
        given(listZonesUseCase.handle()).willReturn(Flux.just(new Zone(UUID.randomUUID(), "A")));

        webTestClient.get().uri("/api/v1/zones")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRenameAZone() {
        var zone = new Zone(UUID.randomUUID(), "Renamed");
        given(renameZoneUseCase.handle(zone.getId(), "Renamed")).willReturn(Mono.just(zone));

        var response = webTestClient.patch().uri("/api/v1/zones/{zoneId}", zone.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name": "Renamed"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ZoneResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Renamed");
    }
}
