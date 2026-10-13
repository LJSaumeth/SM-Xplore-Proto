package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.exceptions.reservation.DifferentProvidersInReservationException;
import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationItemException;
import com.smxplore.proto.domain.exceptions.reservation.MixedReservationItemTypesException;
import com.smxplore.proto.domain.exceptions.reservation.ReservationItemNotFoundException;
import com.smxplore.proto.domain.exceptions.reservation.ReservationItemsNotAllowedException;
import com.smxplore.proto.domain.exceptions.reservation.ReservationNotFoundException;
import com.smxplore.proto.domain.exceptions.reservation.TooManyReservationItemsException;
import com.smxplore.proto.domain.exceptions.service.ServiceNotFoundException;
import com.smxplore.proto.domain.model.reservation.PaymentMethod;
import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.model.service.Service;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.ProviderRef;
import com.smxplore.proto.domain.ports.repository.ReservationItemRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ServiceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ReservationItemUseCasesTest {

    private static final BigDecimal SERVICE_PRICE = new BigDecimal("50000.00");

    @Mock
    private ReservationRepositoryPort reservationRepo;

    @Mock
    private ReservationItemRepositoryPort reservationItemRepo;

    @Mock
    private ServiceRepositoryPort serviceRepo;

    @Captor
    private ArgumentCaptor<Iterable<ReservationItem>> itemsCaptor;

    private AddReservationItemsUseCase addReservationItemsUseCase;
    private AddReservationItemUseCase addReservationItemUseCase;
    private ConsultReservationItemsUseCase consultReservationItemsUseCase;
    private ConsultOneReservationItemUseCase consultOneReservationItemUseCase;
    private ConsultReservationItemsByTypeUseCase consultReservationItemsByTypeUseCase;

    @BeforeEach
    void setUp() {
        addReservationItemsUseCase = new AddReservationItemsUseCase(reservationRepo, reservationItemRepo, serviceRepo);
        addReservationItemUseCase = new AddReservationItemUseCase(addReservationItemsUseCase);
        consultReservationItemsUseCase = new ConsultReservationItemsUseCase(reservationRepo, reservationItemRepo);
        consultOneReservationItemUseCase = new ConsultOneReservationItemUseCase(reservationItemRepo);
        consultReservationItemsByTypeUseCase = new ConsultReservationItemsByTypeUseCase(reservationItemRepo);
    }

    @Test
    void addsOneItemAssigningTheReservationAndUsingTheCatalogPrice() {
        var reservation = reservation();
        // el precio enviado por el cliente (1.00) debe ser reemplazado por el del catálogo (50000.00)
        var item = item(ReservationItemType.SERVICE).toBuilder().price(new BigDecimal("1.00")).build();
        var providerId = UUID.randomUUID();
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId())).willReturn(Flux.empty());
        given(serviceRepo.findById(item.getItemId())).willReturn(Mono.just(service(item.getItemId(), providerId)));
        given(reservationItemRepo.saveAll(any())).willAnswer(invocation -> Flux.fromIterable(invocation.getArgument(0)));
        given(reservationRepo.save(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(addReservationItemUseCase.handle(reservation.getId(), item))
                .assertNext(saved -> assertThat(saved.getReservationId()).isEqualTo(reservation.getId()))
                .verifyComplete();

        verify(reservationItemRepo).saveAll(itemsCaptor.capture());
        var savedItems = StreamSupport.stream(itemsCaptor.getValue().spliterator(), false).toList();
        assertThat(savedItems).hasSize(1);
        assertThat(savedItems.getFirst().getItemId()).isEqualTo(item.getItemId());
        assertThat(savedItems.getFirst().getPrice()).isEqualByComparingTo(SERVICE_PRICE);

        verify(reservationRepo).save(reservation);
        assertThat(reservation.getTotal()).isEqualByComparingTo(SERVICE_PRICE);
    }

    @Test
    void addsSeveralItemsFromTheSameProviderAndRecalculatesTheTotal() {
        var reservation = reservation();
        var providerId = UUID.randomUUID();
        var first = item(ReservationItemType.SERVICE).toBuilder().price(new BigDecimal("1.00")).build();
        var second = item(ReservationItemType.SERVICE).toBuilder().price(new BigDecimal("2.00")).build();
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId())).willReturn(Flux.empty());
        given(serviceRepo.findById(first.getItemId())).willReturn(Mono.just(service(first.getItemId(), providerId)));
        given(serviceRepo.findById(second.getItemId())).willReturn(Mono.just(service(second.getItemId(), providerId)));
        given(reservationItemRepo.saveAll(any())).willAnswer(invocation -> Flux.fromIterable(invocation.getArgument(0)));
        given(reservationRepo.save(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(addReservationItemsUseCase.handle(reservation.getId(), List.of(first, second)))
                .expectNextCount(2)
                .verifyComplete();

        assertThat(reservation.getTotal()).isEqualByComparingTo(SERVICE_PRICE.add(SERVICE_PRICE));
    }

    @Test
    void rejectsAnEmptyRequest() {
        StepVerifier.create(addReservationItemsUseCase.handle(UUID.randomUUID(), List.of()))
                .expectError(InvalidReservationItemException.class)
                .verify();

        verifyNoInteractions(reservationRepo, reservationItemRepo, serviceRepo);
    }

    @Test
    void rejectsMixedTypesInTheSameRequest() {
        var first = item(ReservationItemType.SERVICE);
        var second = item(ReservationItemType.ACTIVITY);

        StepVerifier.create(addReservationItemsUseCase.handle(UUID.randomUUID(), List.of(first, second)))
                .expectError(MixedReservationItemTypesException.class)
                .verify();

        verifyNoInteractions(reservationRepo, reservationItemRepo, serviceRepo);
    }

    @Test
    void rejectsItemsOfADifferentTypeThanTheExistingOnes() {
        var reservation = reservation();
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId()))
                .willReturn(Flux.just(item(ReservationItemType.ACTIVITY)));

        StepVerifier.create(addReservationItemsUseCase.handle(reservation.getId(),
                        List.of(item(ReservationItemType.SERVICE))))
                .expectError(MixedReservationItemTypesException.class)
                .verify();

        verify(reservationItemRepo, never()).saveAll(any());
    }

    @Test
    void rejectsASecondItemForAnEventReservation() {
        var reservation = reservation();
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId()))
                .willReturn(Flux.just(item(ReservationItemType.EVENT)));

        StepVerifier.create(addReservationItemsUseCase.handle(reservation.getId(),
                        List.of(item(ReservationItemType.EVENT))))
                .expectError(TooManyReservationItemsException.class)
                .verify();

        verify(reservationItemRepo, never()).saveAll(any());
    }

    @Test
    void rejectsItemsWhenTheReservationIsInAFinalState() {
        var reservation = reservation(ReservationStatus.CANCELLED);
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));

        StepVerifier.create(addReservationItemUseCase.handle(reservation.getId(),
                        item(ReservationItemType.ACTIVITY)))
                .expectError(ReservationItemsNotAllowedException.class)
                .verify();

        verify(reservationItemRepo, never()).saveAll(any());
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void rejectsMoreItemsThanTheMaximumPerReservation() {
        var reservation = reservation();
        var existing = IntStream.range(0, AddReservationItemsUseCase.MAX_ITEMS_PER_RESERVATION)
                .mapToObj(i -> item(ReservationItemType.ACTIVITY))
                .toList();
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId())).willReturn(Flux.fromIterable(existing));

        StepVerifier.create(addReservationItemUseCase.handle(reservation.getId(),
                        item(ReservationItemType.ACTIVITY)))
                .expectError(TooManyReservationItemsException.class)
                .verify();

        verify(reservationItemRepo, never()).saveAll(any());
    }

    @Test
    void rejectsServicesFromDifferentProviders() {
        var reservation = reservation();
        var existing = item(ReservationItemType.SERVICE);
        var incoming = item(ReservationItemType.SERVICE);
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId())).willReturn(Flux.just(existing));
        given(serviceRepo.findById(existing.getItemId()))
                .willReturn(Mono.just(service(existing.getItemId(), UUID.randomUUID())));
        given(serviceRepo.findById(incoming.getItemId()))
                .willReturn(Mono.just(service(incoming.getItemId(), UUID.randomUUID())));

        StepVerifier.create(addReservationItemsUseCase.handle(reservation.getId(), List.of(incoming)))
                .expectError(DifferentProvidersInReservationException.class)
                .verify();

        verify(reservationItemRepo, never()).saveAll(any());
    }

    @Test
    void rejectsItemsThatReferenceANonExistingService() {
        var reservation = reservation();
        var item = item(ReservationItemType.SERVICE);
        given(reservationRepo.findByIdForUpdate(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId())).willReturn(Flux.empty());
        given(serviceRepo.findById(item.getItemId())).willReturn(Mono.empty());

        StepVerifier.create(addReservationItemUseCase.handle(reservation.getId(), item))
                .expectError(ServiceNotFoundException.class)
                .verify();

        verify(reservationItemRepo, never()).saveAll(any());
    }

    @Test
    void failsWhenTheReservationDoesNotExist() {
        var reservationId = UUID.randomUUID();
        given(reservationRepo.findByIdForUpdate(reservationId)).willReturn(Mono.empty());

        StepVerifier.create(addReservationItemUseCase.handle(reservationId, item(ReservationItemType.EVENT)))
                .expectError(ReservationNotFoundException.class)
                .verify();
    }

    @Test
    void consultsTheItemsOfAReservation() {
        var reservation = reservation();
        var item = item(ReservationItemType.SERVICE);
        given(reservationRepo.findById(reservation.getId())).willReturn(Mono.just(reservation));
        given(reservationItemRepo.findByReservationId(reservation.getId())).willReturn(Flux.just(item));

        StepVerifier.create(consultReservationItemsUseCase.handle(reservation.getId()))
                .expectNext(item)
                .verifyComplete();
    }

    @Test
    void consultsTheItemsOfAReservationFailsWhenItDoesNotExist() {
        var reservationId = UUID.randomUUID();
        given(reservationRepo.findById(reservationId)).willReturn(Mono.empty());

        StepVerifier.create(consultReservationItemsUseCase.handle(reservationId))
                .expectError(ReservationNotFoundException.class)
                .verify();
    }

    @Test
    void consultsOneItemAndFailsWhenItDoesNotExist() {
        var item = item(ReservationItemType.EVENT);
        given(reservationItemRepo.findById(item.getId())).willReturn(Mono.just(item));

        StepVerifier.create(consultOneReservationItemUseCase.handle(item.getId()))
                .expectNext(item)
                .verifyComplete();

        var unknownId = UUID.randomUUID();
        given(reservationItemRepo.findById(unknownId)).willReturn(Mono.empty());

        StepVerifier.create(consultOneReservationItemUseCase.handle(unknownId))
                .expectError(ReservationItemNotFoundException.class)
                .verify();
    }

    @Test
    void consultsTheItemsByType() {
        var item = item(ReservationItemType.ACTIVITY);
        given(reservationItemRepo.findByItemType(ReservationItemType.ACTIVITY, 50)).willReturn(Flux.just(item));

        StepVerifier.create(consultReservationItemsByTypeUseCase.handle(ReservationItemType.ACTIVITY, 50))
                .expectNext(item)
                .verifyComplete();
    }

    private Reservation reservation() {
        return reservation(ReservationStatus.PENDING);
    }

    private Reservation reservation(ReservationStatus status) {
        return Reservation.builder()
                .id(UUID.randomUUID())
                .touristId(UUID.randomUUID())
                .numberOfPeople(2)
                .paymentMethod(PaymentMethod.CARD)
                .status(status)
                .bookedFor(Instant.now().plusSeconds(3600))
                .build();
    }

    private ReservationItem item(ReservationItemType type) {
        return ReservationItem.builder()
                .id(UUID.randomUUID())
                .itemId(UUID.randomUUID())
                .type(type)
                .price(SERVICE_PRICE)
                .build();
    }

    private Service service(UUID id, UUID providerId) {
        return Service.builder()
                .id(id)
                .name("Airport transport")
                .type(ServiceType.TRANSPORT)
                .provider(new ProviderRef(providerId, "Ana"))
                .location(new Location(4.65, -74.05))
                .price(SERVICE_PRICE)
                .build();
    }
}
