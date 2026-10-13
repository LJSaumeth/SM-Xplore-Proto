package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReservationRepositoryPort {
    Mono<Reservation> save(Reservation reservation);

    Mono<Reservation> findById(UUID id);

    /**
     * Lectura con lock pesimista de la fila (SELECT ... FOR UPDATE) para
     * serializar las operaciones que mutan la reserva. Debe ejecutarse dentro
     * de una transacción; de lo contrario el lock se libera de inmediato.
     */
    Mono<Reservation> findByIdForUpdate(UUID id);

    Flux<Reservation> findAllByTouristId(UUID touristId, int limit);

    Flux<Reservation> findAllByStatus(ReservationStatus status, int limit);
}
