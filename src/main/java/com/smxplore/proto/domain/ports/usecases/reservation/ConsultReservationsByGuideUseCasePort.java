package com.smxplore.proto.domain.ports.usecases.reservation;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;

import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ConsultReservationsByGuideUseCasePort {

    /** Lists the reservations received by a guide; {@code status} is optional (null = any). */
    Flux<Reservation> handle(UUID guideId, ReservationStatus status, int limit);
}
