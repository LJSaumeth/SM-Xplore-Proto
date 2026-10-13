package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.GuideEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface GuideR2dbcRepository extends ReactiveCrudRepository<GuideEntity, UUID> {
}
