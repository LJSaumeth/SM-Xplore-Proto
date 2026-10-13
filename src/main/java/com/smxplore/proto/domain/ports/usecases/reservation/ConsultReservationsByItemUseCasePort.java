package com.smxplore.proto.domain.ports.usecases.reservation;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationItemType;

import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ConsultReservationsByItemUseCasePort {

    /** Lists the reservations that contain a given event, activity or service. */
    Flux<Reservation> handle(UUID itemId, ReservationItemType type, int limit);
}
