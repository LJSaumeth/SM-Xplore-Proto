package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.exceptions.reservation.ReservationNotFoundException;
import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.ports.repository.ReservationItemRepositoryPort;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationItemsUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ConsultReservationItemsUseCase implements ConsultReservationItemsUseCasePort {
    private final ReservationRepositoryPort reservationRepo;
    private final ReservationItemRepositoryPort reservationItemRepo;

    @Override
    public Flux<ReservationItem> handle(UUID reservationId) {
        return reservationRepo.findById(reservationId)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException(reservationId)))
                .flatMapMany(reservation -> reservationItemRepo.findByReservationId(reservationId));
    }
}
