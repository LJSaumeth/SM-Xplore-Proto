package com.smxplore.proto.domain.ports.usecases.reservation;

import com.smxplore.proto.domain.model.reservation.ReservationItem;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ConsultReservationItemsUseCasePort {
    Flux<ReservationItem> handle(UUID reservationId);
}
