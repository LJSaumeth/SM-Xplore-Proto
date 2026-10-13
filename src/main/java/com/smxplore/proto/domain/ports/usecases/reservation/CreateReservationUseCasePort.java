package com.smxplore.proto.domain.ports.usecases.reservation;

import com.smxplore.proto.domain.model.reservation.Reservation;
import reactor.core.publisher.Mono;

public interface CreateReservationUseCasePort {
    Mono<Reservation> handle(Reservation reservation);
}