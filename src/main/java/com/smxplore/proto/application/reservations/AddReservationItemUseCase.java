package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationItemException;
import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemsUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

/**
 * Agregar un item es agregar una colección de un solo item, así que todas las
 * validaciones viven en {@link AddReservationItemsUseCase}.
 */
@RequiredArgsConstructor
public class AddReservationItemUseCase implements AddReservationItemUseCasePort {

    private final AddReservationItemsUseCasePort addReservationItems;

    @Override
    public Mono<ReservationItem> handle(UUID reservationId, ReservationItem item) {
        return addReservationItems.handle(reservationId, Collections.singletonList(item))
                .switchIfEmpty(Mono.error(new InvalidReservationItemException(
                        "The reservation item could not be saved.")))
                .single();
    }
}
