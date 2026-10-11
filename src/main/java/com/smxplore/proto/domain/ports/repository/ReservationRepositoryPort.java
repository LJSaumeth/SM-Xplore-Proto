package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReservationRepositoryPort {
    Mono<Reservation> save(Reservation reservation);

    Mono<Reservation> findById(UUID id);

    Flux<Reservation> findAllByTouristId(UUID touristId);

    Flux<Reservation> findAllByStatus(ReservationStatus status);
}
