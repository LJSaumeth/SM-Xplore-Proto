package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.exceptions.reservation.ReservationItemNotFoundException;
import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.ports.repository.ReservationItemRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultOneReservationItemUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ConsultOneReservationItemUseCase implements ConsultOneReservationItemUseCasePort {
    private final ReservationItemRepositoryPort reservationItemRepo;

    @Override
    public Mono<ReservationItem> handle(UUID reservationItemId) {
        return reservationItemRepo.findById(reservationItemId)
                .switchIfEmpty(Mono.error(new ReservationItemNotFoundException(reservationItemId)));
    }
}
