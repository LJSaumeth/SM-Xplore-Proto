package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ReservationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReservationR2dbcRepository extends ReactiveCrudRepository<ReservationEntity, UUID> {

    Flux<ReservationEntity> findAllByTouristId(UUID touristId, Pageable pageable);

    Flux<ReservationEntity> findAllByStatus(ReservationStatus status, Pageable pageable);

    @Query("SELECT * FROM reservations WHERE id = :id FOR UPDATE")
    Mono<ReservationEntity> findByIdForUpdate(UUID id);
}
