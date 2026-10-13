package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.EventEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.UUID;

public interface EventR2dbcRepository extends ReactiveCrudRepository<EventEntity, UUID> {

    Flux<EventEntity> findByNameContainingIgnoreCase(String name);

    Flux<EventEntity> findAllBySiteId(UUID siteId);

    Flux<EventEntity> findAllBySiteIdIn(Collection<UUID> siteIds);
}
