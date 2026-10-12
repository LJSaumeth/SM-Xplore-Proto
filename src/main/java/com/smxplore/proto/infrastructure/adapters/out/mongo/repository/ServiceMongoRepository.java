package com.smxplore.proto.infrastructure.adapters.out.mongo.repository;

import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.ServiceDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ServiceMongoRepository extends ReactiveMongoRepository<ServiceDocument, UUID> {

    Flux<ServiceDocument> findAllByStatusAndType(ServiceStatus status, ServiceType type);

    Flux<ServiceDocument> findAllByStatus(ServiceStatus status);

    Flux<ServiceDocument> findAllByProvider_Id(UUID providerId);

    Flux<ServiceDocument> findAllByProvider_Name(String providerName);
}
