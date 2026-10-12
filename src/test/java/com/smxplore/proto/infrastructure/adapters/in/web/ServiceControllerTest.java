package com.smxplore.proto.infrastructure.adapters.in.web;

import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.model.types.Attribute;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.ProviderRef;
import com.smxplore.proto.domain.ports.usecases.service.AddAttributeToServiceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.AddServicePhotosUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.ChangeServicePriceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServiceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByProviderUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByTypeUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.MarkServiceAsAvailableUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.MarkServiceAsUnavailableUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.RegisterServiceUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.controller.ServiceController;
import com.smxplore.proto.infrastructure.adapters.in.web.error.GlobalExceptionHandler;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ServiceWebMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = ServiceController.class)
@Import({ServiceWebMapperImpl.class, GlobalExceptionHandler.class})
class ServiceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterServiceUseCasePort registerServiceUseCase;

    @MockitoBean
    private FindServiceUseCasePort findServiceUseCase;

    @MockitoBean
    private FindServicesByStatusUseCasePort findServicesByStatusUseCase;

    @MockitoBean
    private FindServicesByTypeUseCasePort findServicesByTypeUseCase;

    @MockitoBean
    private FindServicesByProviderUseCasePort findServicesByProviderUseCase;

    @MockitoBean
    private ChangeServicePriceUseCasePort changeServicePriceUseCase;

    @MockitoBean
    private MarkServiceAsAvailableUseCasePort markServiceAsAvailableUseCase;

    @MockitoBean
    private MarkServiceAsUnavailableUseCasePort markServiceAsUnavailableUseCase;

    @MockitoBean
    private AddServicePhotosUseCasePort addServicePhotosUseCase;

    @MockitoBean
    private AddAttributeToServiceUseCasePort addAttributeToServiceUseCase;

    @Autowired
    void configureClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser("tester"))
                .mutateWith(SecurityMockServerConfigurers.csrf());
    }

    @Test
    void registerMapsRequestAndReturnsCreatedService() {
        var provider = new ProviderRef(UUID.randomUUID(), "Ana");
        var registered = Service.builder()
                .name("Airport transport")
                .type(ServiceType.TRANSPORT)
                .provider(provider)
                .location(new Location(4.65, -74.05))
                .price(new BigDecimal("120000.50"))
                .build();
        registered.addExtra(Attribute.builder().name("wifi").value(true).build());

        given(registerServiceUseCase.handle(any(), argThat(service ->
                "Airport transport".equals(service.getName())
                        && ServiceType.TRANSPORT.equals(service.getType())
                        && service.getPrice().compareTo(new BigDecimal("120000.50")) == 0
                        && service.getExtras().contains("wifi")
                        && new Location(4.65, -74.05).equals(service.getLocation()))))
                .willReturn(Mono.just(registered));

        webTestClient.post().uri("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "provider": {"id": "%s", "name": "Ana"},
                          "name": "Airport transport",
                          "type": "TRANSPORT",
                          "price": 120000.50,
                          "location": {"latitude": 4.65, "longitude": -74.05},
                          "photosUrls": ["http://photo1"],
                          "extras": [{"name": "wifi", "value": true}]
                        }
                        """.formatted(provider.id()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(registered.getId().toString())
                .jsonPath("$.name").isEqualTo("Airport transport")
                .jsonPath("$.status").isEqualTo(ServiceStatus.AVAILABLE.name())
                .jsonPath("$.price").isEqualTo(120000.50)
                .jsonPath("$.provider.name").isEqualTo("Ana")
                .jsonPath("$.extras[0].name").isEqualTo("wifi")
                .jsonPath("$.extras[0].value").isEqualTo(true);
    }

    @Test
    void registerRejectsInvalidRequest() {
        webTestClient.post().uri("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "provider": {"id": "%s", "name": "Ana"},
                          "name": "  ",
                          "type": "TRANSPORT",
                          "price": 10,
                          "location": {"latitude": 4.65, "longitude": -74.05}
                        }
                        """.formatted(UUID.randomUUID()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void findOneReturnsNotFoundWhenTheServiceDoesNotExist() {
        var serviceId = UUID.randomUUID();
        given(findServiceUseCase.handle(serviceId)).willReturn(Mono.error(new ServiceNotFoundException(serviceId)));

        webTestClient.get().uri("/api/v1/services/{serviceId}", serviceId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.path").isEqualTo("/api/v1/services/" + serviceId);
    }
}
