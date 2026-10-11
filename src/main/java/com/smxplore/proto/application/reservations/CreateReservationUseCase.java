package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.exceptions.reservation.InvalidReservationRequestException;
import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.CreateReservationUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RequiredArgsConstructor
public class CreateReservationUseCase implements CreateReservationUseCasePort {
    private final ReservationRepositoryPort reservationRepo;

    @Override
    public Mono<Reservation> handle(Reservation reservation) {
        return Mono.justOrEmpty(reservation)
                .switchIfEmpty(Mono.error(new InvalidReservationRequestException("Reservation cannot be null")))
                .map(this::validateAndBuild)
                .flatMap(reservationRepo::save);
    }

    private Reservation validateAndBuild(Reservation reservation) {
        var pending = reservation.toBuilder()
                .status(ReservationStatus.PENDING)
                .bookedAt(Instant.now())
                .build();
        pending.validate();
        return pending;
    }
}
