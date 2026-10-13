package com.smxplore.proto.domain.ports.usecases.reservation;

import com.smxplore.proto.domain.model.reservation.ReservationItem;
import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import reactor.core.publisher.Flux;

public interface ConsultReservationItemsByTypeUseCasePort {
    Flux<ReservationItem> handle(ReservationItemType type, int limit);
}
