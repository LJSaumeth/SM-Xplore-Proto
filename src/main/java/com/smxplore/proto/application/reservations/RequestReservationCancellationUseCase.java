package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.exceptions.reservation.ReservationNotFoundException;
import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.RequestReservationCancellationUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class RequestReservationCancellationUseCase implements RequestReservationCancellationUseCasePort {
    private final ReservationRepositoryPort reservationRepo;

    @Override
    public Mono<Reservation> handle(UUID reservationId) {
        return reservationRepo.findByIdForUpdate(reservationId)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException(reservationId)))
                .doOnNext(Reservation::requestCancellation)
                .flatMap(reservationRepo::save);
    }
}
