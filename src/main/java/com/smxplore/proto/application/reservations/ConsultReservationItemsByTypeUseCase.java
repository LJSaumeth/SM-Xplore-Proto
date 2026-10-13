package com.smxplore.proto.application.reservations;

import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import com.smxplore.proto.domain.ports.repository.ReservationItemRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationItemsByTypeUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ConsultReservationItemsByTypeUseCase implements ConsultReservationItemsByTypeUseCasePort {
    private final ReservationItemRepositoryPort reservationItemRepo;

    @Override
    public Flux<ReservationItem> handle(ReservationItemType type, int limit) {
        return reservationItemRepo.findByItemType(type, limit);
    }
}
