package com.smxplore.proto.domain.ports.usecases.reservation;

import com.smxplore.proto.domain.model.reservation.Reservation;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RegisterReservationAttendanceUseCasePort {
    Mono<Reservation> handle(UUID reservationId);
}
