package com.smxplore.proto.infrastructure.adapters.out.postgres;

import com.smxplore.proto.domain.model.zone.Zone;
import com.smxplore.proto.domain.ports.repository.IZoneRepository;
import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ZoneEntity;
import com.smxplore.proto.infrastructure.adapters.out.postgres.repository.ZoneR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ZonePostgresAdapter implements IZoneRepository {

    private final ZoneR2dbcRepository repository;
    private final R2dbcEntityTemplate template;

    /**
     * R2DBC has no upsert, so an explicit existence check decides between insert and update.
     */
    @Override
    public Mono<Zone> save(Zone zone) {
        var entity = toEntity(zone);
        return repository.existsById(entity.id())
                .flatMap(exists -> exists ? template.update(entity) : template.insert(entity))
                .map(ZonePostgresAdapter::toDomain);
    }

    @Override
    public Mono<Zone> findById(UUID id) {
        return repository.findById(id).map(ZonePostgresAdapter::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Flux<Zone> findAll() {
        return repository.findAll().map(ZonePostgresAdapter::toDomain);
    }

    private static ZoneEntity toEntity(Zone zone) {
        return new ZoneEntity(zone.getId(), zone.getName());
    }

    private static Zone toDomain(ZoneEntity entity) {
        return new Zone(entity.id(), entity.name());
    }
}
