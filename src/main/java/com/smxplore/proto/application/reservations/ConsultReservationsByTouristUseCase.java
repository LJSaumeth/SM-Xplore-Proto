package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByTouristUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class ConsultReservationsByTouristUseCase implements ConsultReservationsByTouristUseCasePort {
    private final ReservationRepositoryPort reservationRepo;

    @Override
    public Flux<Reservation> handle(UUID touristId, int limit) {
        return reservationRepo.findAllByTouristId(touristId, limit);
    }
}
