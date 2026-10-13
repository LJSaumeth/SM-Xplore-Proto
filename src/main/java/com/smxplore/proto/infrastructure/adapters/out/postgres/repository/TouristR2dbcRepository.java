package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.TouristEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TouristR2dbcRepository extends ReactiveCrudRepository<TouristEntity, UUID> {
}
