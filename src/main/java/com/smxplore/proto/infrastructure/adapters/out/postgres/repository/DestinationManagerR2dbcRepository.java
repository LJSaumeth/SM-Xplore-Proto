package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.DestinationManagerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface DestinationManagerR2dbcRepository extends ReactiveCrudRepository<DestinationManagerEntity, UUID> {
}
