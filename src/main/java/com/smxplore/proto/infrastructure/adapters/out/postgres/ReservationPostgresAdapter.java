package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.model.reservation.Reservation;
import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.ports.repository.ReservationRepositoryPort;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ReservationEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.mapper.ReservationPersistenceMapper;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.ReservationR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationPostgresAdapter implements ReservationRepositoryPort {

    private static final Sort STABLE_ORDER = Sort.by("id");

    private final ReservationR2dbcRepository repository;
    private final R2dbcEntityTemplate template;
    private final ReservationPersistenceMapper mapper;

    /**
     * Los ids se generan en el dominio, por lo que el save() de Spring Data no
     * puede decidir entre insert y update con isNew() (haría siempre update).
     * Aquí se consulta la existencia y se hace el upsert explícito.
     */
    @Override
    public Mono<Reservation> save(Reservation reservation) {
        var entity = mapper.toEntity(reservation);
        return saveEntity(entity).map(mapper::toDomain);
    }

    @Override
    public Mono<Reservation> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Mono<Reservation> findByIdForUpdate(UUID id) {
        return repository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Reservation> findAllByTouristId(UUID touristId, int limit) {
        return repository.findAllByTouristId(touristId, page(limit)).map(mapper::toDomain);
    }

    @Override
    public Flux<Reservation> findAllByStatus(ReservationStatus status, int limit) {
        return repository.findAllByStatus(status, page(limit)).map(mapper::toDomain);
    }

    private PageRequest page(int limit) {
        return PageRequest.of(0, limit, STABLE_ORDER);
    }

    private Mono<ReservationEntity> saveEntity(ReservationEntity entity) {
        return repository.existsById(entity.id())
                .flatMap(exists -> exists ? repository.save(entity) : template.insert(entity));
    }
}
