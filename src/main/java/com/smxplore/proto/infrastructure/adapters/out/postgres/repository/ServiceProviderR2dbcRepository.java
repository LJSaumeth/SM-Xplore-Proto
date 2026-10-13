package com.smxplore.proto.infrastructure.adapters.out.postgres.repository;

import com.smxplore.proto.infrastructure.adapters.out.postgres.entity.ServiceProviderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ServiceProviderR2dbcRepository extends ReactiveCrudRepository<ServiceProviderEntity, UUID> {
}
