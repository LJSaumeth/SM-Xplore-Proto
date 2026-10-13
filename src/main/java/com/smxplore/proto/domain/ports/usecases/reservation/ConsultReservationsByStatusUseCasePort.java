package com.smxplore.proto.domain.ports.usecases.reservation;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import reactor.core.publisher.Flux;

public interface ConsultReservationsByStatusUseCasePort {
    Flux<Reservation> handle(ReservationStatus status, int limit);
}
