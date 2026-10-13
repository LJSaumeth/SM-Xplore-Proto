package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByStatusUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ConsultReservationsByStatusUseCase implements ConsultReservationsByStatusUseCasePort {
    private final ReservationRepositoryPort reservationRepo;

    @Override
    public Flux<Reservation> handle(ReservationStatus status, int limit) {
        return reservationRepo.findAllByStatus(status, limit);
    }
}
