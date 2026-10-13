package com.smxplore.proto.infrastructure.adapters.in.web;

import com.smxplore.proto.domain.exceptions.reservation.ReservationNotFoundException;
import com.smxplore.proto.domain.model.reservation.PaymentMethod;
import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.CancelReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConfirmReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultOneReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByGuideUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByItemUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByProviderUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByTouristUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.CreateReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RegisterReservationAttendanceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RegisterReservationNoShowUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RejectReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RequestReservationCancellationUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.controller.ReservationController;
import com.smxplore.proto.infrastructure.adapters.in.web.error.GlobalExceptionHandler;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ReservationWebMapperImpl;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@WebFluxTest(controllers = ReservationController.class)
@Import({ReservationWebMapperImpl.class, GlobalExceptionHandler.class})
class ReservationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateReservationUseCasePort createReservationUseCase;

    @MockitoBean
    private ConsultOneReservationUseCasePort consultOneReservationUseCase;

    @MockitoBean
    private ConsultReservationsByStatusUseCasePort consultReservationsByStatusUseCase;

    @MockitoBean
    private ConsultReservationsByTouristUseCasePort consultReservationsByTouristUseCase;

    @MockitoBean
    private ConsultReservationsByProviderUseCasePort consultReservationsByProviderUseCase;

    @MockitoBean
    private ConsultReservationsByGuideUseCasePort consultReservationsByGuideUseCase;

    @MockitoBean
    private ConsultReservationsByItemUseCasePort consultReservationsByItemUseCase;

    @MockitoBean
    private ServiceRepositoryPort serviceRepository;

    @MockitoBean
    private ActivityRepositoryPort activityRepository;

    @MockitoBean
    private ConfirmReservationUseCasePort confirmReservationUseCase;

    @MockitoBean
    private RejectReservationUseCasePort rejectReservationUseCase;

    @MockitoBean
    private CancelReservationUseCasePort cancelReservationUseCase;

    @MockitoBean
    private RequestReservationCancellationUseCasePort requestReservationCancellationUseCase;

    @MockitoBean
    private RegisterReservationAttendanceUseCasePort registerReservationAttendanceUseCase;

    @MockitoBean
    private RegisterReservationNoShowUseCasePort registerReservationNoShowUseCase;

    @Autowired
    void configureClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .jwt(jwt -> jwt.subject("tester").claim("role", "ADMIN")))
                .mutateWith(SecurityMockServerConfigurers.csrf());
    }

    @Test
    void createMapsTheRequestAndReturnsTheCreatedReservation() {
        var touristId = UUID.randomUUID();
        var bookedFor = Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.SECONDS);
        var reservation = Reservation.builder()
                .touristId(touristId)
                .numberOfPeople(2)
                .total(BigDecimal.ZERO)
                .paymentMethod(PaymentMethod.CARD)
                .bookedFor(bookedFor)
                .build();
        given(createReservationUseCase.handle(argThat(created ->
                touristId.equals(created.getTouristId())
                        && created.getNumberOfPeople() == 2
                        && created.getPaymentMethod() == PaymentMethod.CARD
                        && created.getTotal().compareTo(BigDecimal.ZERO) == 0)))
                .willReturn(Mono.just(reservation));

        webTestClient.post().uri("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "touristId": "%s",
                          "numberOfPeople": 2,
                          "paymentMethod": "CARD",
                          "bookedFor": "%s"
                        }
                        """.formatted(touristId, bookedFor))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(reservation.getId().toString())
                .jsonPath("$.touristId").isEqualTo(touristId.toString())
                .jsonPath("$.status").isEqualTo(ReservationStatus.PENDING.name())
                .jsonPath("$.total").isEqualTo(0)
                .jsonPath("$.paymentMethod").isEqualTo(PaymentMethod.CARD.name());
    }

    @Test
    void createRejectsAnInvalidRequest() {
        webTestClient.post().uri("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "touristId": "%s",
                          "numberOfPeople": 0,
                          "paymentMethod": "CARD",
                          "bookedFor": "%s"
                        }
                        """.formatted(UUID.randomUUID(), Instant.now().plusSeconds(86400)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void findOneReturnsNotFoundWhenTheReservationDoesNotExist() {
        var reservationId = UUID.randomUUID();
        given(consultOneReservationUseCase.handle(reservationId))
                .willReturn(Mono.error(new ReservationNotFoundException(reservationId)));

        webTestClient.get().uri("/api/v1/reservations/{reservationId}", reservationId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.path").isEqualTo("/api/v1/reservations/" + reservationId);
    }

    @Test
    void confirmReturnsTheConfirmedReservation() {
        var reservation = reservation(ReservationStatus.CONFIRMED);
        given(confirmReservationUseCase.handle(reservation.getId())).willReturn(Mono.just(reservation));

        webTestClient.patch().uri("/api/v1/reservations/{reservationId}/confirm", reservation.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(reservation.getId().toString())
                .jsonPath("$.status").isEqualTo(ReservationStatus.CONFIRMED.name());
    }

    @Test
    void createRejectsReservingForAnotherTourist() {
        var client = webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()
                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()).claim("role", "TOURIST")));

        client.post().uri("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "touristId": "%s",
                          "numberOfPeople": 2,
                          "paymentMethod": "CARD",
                          "bookedFor": "%s"
                        }
                        """.formatted(UUID.randomUUID(), Instant.now().plusSeconds(86400)))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void confirmRejectsWhenTheCallerIsNotTheProviderOrGuide() {
        var reservation = reservation(ReservationStatus.PENDING).toBuilder()
                .providerId(UUID.randomUUID())
                .guideId(UUID.randomUUID())
                .build();
        given(consultOneReservationUseCase.handle(reservation.getId())).willReturn(Mono.just(reservation));

        var client = webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()
                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()).claim("role", "SERVICE_PROVIDER")));

        client.patch().uri("/api/v1/reservations/{reservationId}/confirm", reservation.getId())
                .exchange()
                .expectStatus().isForbidden();

        verify(confirmReservationUseCase, never()).handle(any());
    }

    private Reservation reservation(ReservationStatus status) {
        return Reservation.builder()
                .touristId(UUID.randomUUID())
                .numberOfPeople(2)
                .total(new BigDecimal("55000.00"))
                .paymentMethod(PaymentMethod.CASH)
                .status(status)
                .bookedFor(Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}
