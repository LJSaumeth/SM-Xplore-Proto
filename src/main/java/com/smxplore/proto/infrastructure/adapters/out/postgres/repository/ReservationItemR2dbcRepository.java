package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ReservationItemEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ReservationItemR2dbcRepository extends ReactiveCrudRepository<ReservationItemEntity, UUID> {

    Flux<ReservationItemEntity> findByReservationId(UUID reservationId);

    Flux<ReservationItemEntity> findByItemId(UUID itemId);

    Flux<ReservationItemEntity> findByType(ReservationItemType type, Pageable pageable);
}
