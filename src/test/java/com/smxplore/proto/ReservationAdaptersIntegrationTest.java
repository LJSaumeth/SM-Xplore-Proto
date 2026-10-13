package com.smxplore.proto;

import com.smxplore.proto.domain.exceptions.activity.ActivityNotFoundException;
import com.smxplore.proto.domain.model.activity.Activity;
import com.smxplore.proto.domain.model.reservation.PaymentMethod;
import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.ports.repository.ActivityRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationItemRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemsUseCasePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ReservationAdaptersIntegrationTest {

    @Autowired
    private ReservationRepositoryPort reservationRepository;

    @Autowired
    private ReservationItemRepositoryPort reservationItemRepository;

    @Autowired
    private AddReservationItemsUseCasePort addReservationItemsUseCase;

    @Autowired
    private ActivityRepositoryPort activityRepository;

    @Test
    void reservationRoundTripAndUpdates() {
        var reservation = reservation(new BigDecimal("150000.00"));
        var saved = reservationRepository.save(reservation).block();

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(reservation.getId());

        var found = reservationRepository.findById(reservation.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getTouristId()).isEqualTo(reservation.getTouristId());
        assertThat(found.getNumberOfPeople()).isEqualTo(3);
        assertThat(found.getTotal()).isEqualByComparingTo(new BigDecimal("150000.00"));
        assertThat(found.getPaymentMethod()).isEqualTo(PaymentMethod.TRANSFER);
        assertThat(found.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(found.getBookedAt()).isNotNull();
        assertThat(found.getBookedFor()).isEqualTo(reservation.getBookedFor());
        assertThat(found.getNotes()).isEqualTo("Frente al parque");

        var updated = found.toBuilder().notes("Actualizado").build();
        reservationRepository.save(updated).block();
        assertThat(reservationRepository.findById(reservation.getId()).block().getNotes()).isEqualTo("Actualizado");

        assertThat(reservationRepository.findAllByTouristId(reservation.getTouristId(), 50).collectList().block())
                .extracting(Reservation::getId).contains(reservation.getId());
        assertThat(reservationRepository.findAllByStatus(ReservationStatus.PENDING, 50).collectList().block())
                .extracting(Reservation::getId).contains(reservation.getId());
    }

    @Test
    void providerGuideAndCodeRoundTripAndProviderQueryWorks() {
        var providerId = UUID.randomUUID();
        var guideId = UUID.randomUUID();
        var reservation = reservationRepository.save(reservation(new BigDecimal("0.00"))
                .toBuilder().providerId(providerId).guideId(guideId).build()).block();

        assertThat(reservation).isNotNull();
        assertThat(reservation.getCode()).startsWith("R-");

        var found = reservationRepository.findById(reservation.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getCode()).startsWith("R-");
        assertThat(found.getProviderId()).isEqualTo(providerId);
        assertThat(found.getGuideId()).isEqualTo(guideId);

        assertThat(reservationRepository.findAllByProviderId(providerId, null, 50).collectList().block())
                .extracting(Reservation::getId).contains(reservation.getId());
        assertThat(reservationRepository.findAllByProviderId(providerId, ReservationStatus.PENDING, 50)
                .collectList().block())
                .extracting(Reservation::getId).contains(reservation.getId());
        assertThat(reservationRepository.findAllByGuideId(guideId, null, 50).collectList().block())
                .extracting(Reservation::getId).contains(reservation.getId());

        var item = reservationItemRepository.saveAll(List.of(item(reservation.getId(), new BigDecimal("1000.00"))))
                .collectList().block().getFirst();
        assertThat(reservationRepository.findAllByItem(item.getItemId(), item.getType(), 50).collectList().block())
                .extracting(Reservation::getId).contains(reservation.getId());
    }

    @Test
    void itemsRoundTripAndAllQueries() {
        var reservation = reservationRepository.save(reservation(new BigDecimal("0.00"))).block();
        var first = item(reservation.getId(), new BigDecimal("25000.00"));
        var second = item(reservation.getId(), new BigDecimal("30000.00"));

        var saved = reservationItemRepository.saveAll(List.of(first, second)).collectList().block();
        assertThat(saved).hasSize(2)
                .extracting(ReservationItem::getId)
                .containsExactlyInAnyOrder(first.getId(), second.getId());

        var found = reservationItemRepository.findById(first.getId()).block();
        assertThat(found).isNotNull();
        assertThat(found.getReservationId()).isEqualTo(reservation.getId());
        assertThat(found.getItemId()).isEqualTo(first.getItemId());
        assertThat(found.getType()).isEqualTo(ReservationItemType.ACTIVITY);
        assertThat(found.getPrice()).isEqualByComparingTo(new BigDecimal("25000.00"));

        assertThat(reservationItemRepository.findByReservationId(reservation.getId()).collectList().block())
                .hasSize(2);
        assertThat(reservationItemRepository.findByItemId(first.getItemId()).collectList().block())
                .extracting(ReservationItem::getId).containsExactly(first.getId());
        assertThat(reservationItemRepository.findByItemType(ReservationItemType.ACTIVITY, 50).collectList().block())
                .extracting(ReservationItem::getId).contains(first.getId(), second.getId());
    }

    @Test
    void addingItemsRecalculatesTheReservationTotal() {
        var reservation = reservationRepository.save(reservation(new BigDecimal("0.00"))).block();
        var firstActivity = activityRepository.save(activity(new BigDecimal("25000.00"))).block();
        var secondActivity = activityRepository.save(activity(new BigDecimal("30000.00"))).block();
        var first = item(reservation.getId(), firstActivity.getId());
        var second = item(reservation.getId(), secondActivity.getId());

        StepVerifier.create(addReservationItemsUseCase.handle(reservation.getId(), List.of(first, second)))
                .expectNextCount(2)
                .verifyComplete();

        var updated = reservationRepository.findById(reservation.getId()).block();
        assertThat(updated).isNotNull();
        assertThat(updated.getTotal()).isEqualByComparingTo(new BigDecimal("55000.00"));
        assertThat(reservationItemRepository.findByReservationId(reservation.getId()).collectList().block())
                .hasSize(2);
    }

    @Test
    void addingItemsIsTransactional() {
        var reservation = reservationRepository.save(reservation(new BigDecimal("0.00"))).block();
        var firstActivity = activityRepository.save(activity(new BigDecimal("25000.00"))).block();
        var first = item(reservation.getId(), firstActivity.getId());
        // el segundo item referencia una actividad inexistente: el error ocurre dentro de la
        // transacción y no debe quedar ningún item ni cambio de total
        var missing = item(reservation.getId(), new BigDecimal("30000.00"));

        StepVerifier.create(addReservationItemsUseCase.handle(reservation.getId(), List.of(first, missing)))
                .expectError(ActivityNotFoundException.class)
                .verify();

        assertThat(reservationItemRepository.findByReservationId(reservation.getId()).collectList().block()).isEmpty();
        assertThat(reservationRepository.findById(reservation.getId()).block().getTotal())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void concurrentItemAdditionsSerializeOnTheReservationLock() {
        var reservation = reservationRepository.save(reservation(new BigDecimal("0.00"))).block();
        var firstActivity = activityRepository.save(activity(new BigDecimal("10000.00"))).block();
        var secondActivity = activityRepository.save(activity(new BigDecimal("20000.00"))).block();
        var first = item(reservation.getId(), firstActivity.getId());
        var second = item(reservation.getId(), secondActivity.getId());

        var both = Mono.zip(
                        addReservationItemsUseCase.handle(reservation.getId(), List.of(first)).collectList(),
                        addReservationItemsUseCase.handle(reservation.getId(), List.of(second)).collectList())
                .subscribeOn(Schedulers.boundedElastic());

        StepVerifier.create(both)
                .assertNext(tuple -> assertThat(tuple.getT1()).hasSize(1))
                .verifyComplete();

        assertThat(reservationItemRepository.findByReservationId(reservation.getId()).collectList().block())
                .hasSize(2);
        assertThat(reservationRepository.findById(reservation.getId()).block().getTotal())
                .isEqualByComparingTo(new BigDecimal("30000.00"));
    }

    private Reservation reservation(BigDecimal total) {
        return Reservation.builder()
                .touristId(UUID.randomUUID())
                .numberOfPeople(3)
                .total(total)
                .paymentMethod(PaymentMethod.TRANSFER)
                .bookedFor(Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.MILLIS))
                .notes("Frente al parque")
                .build();
    }

    private ReservationItem item(UUID reservationId, BigDecimal price) {
        return ReservationItem.builder()
                .reservationId(reservationId)
                .itemId(UUID.randomUUID())
                .type(ReservationItemType.ACTIVITY)
                .price(price)
                .build();
    }

    private ReservationItem item(UUID reservationId, UUID itemId) {
        return ReservationItem.builder()
                .reservationId(reservationId)
                .itemId(itemId)
                .type(ReservationItemType.ACTIVITY)
                .price(new BigDecimal("1.00"))
                .build();
    }

    private Activity activity(BigDecimal price) {
        Instant startsAt = Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.MILLIS);
        return Activity.builder()
                .id(UUID.randomUUID())
                .siteId(UUID.randomUUID())
                .name("City Walking Tour")
                .category("Tour")
                .price(price)
                .maxCapacity(20)
                .durationMinutes(90)
                .startsAt(startsAt)
                .endsAt(startsAt.plusSeconds(5400))
                .build();
    }
}
