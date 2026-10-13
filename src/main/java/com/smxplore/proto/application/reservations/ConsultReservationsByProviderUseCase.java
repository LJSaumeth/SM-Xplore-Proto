package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByProviderUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class ConsultReservationsByProviderUseCase implements ConsultReservationsByProviderUseCasePort {

    private final ReservationRepositoryPort reservationRepo;

    @Override
    public Flux<Reservation> handle(UUID providerId, ReservationStatus status, int limit) {
        return reservationRepo.findAllByProviderId(providerId, status, limit);
    }
}
