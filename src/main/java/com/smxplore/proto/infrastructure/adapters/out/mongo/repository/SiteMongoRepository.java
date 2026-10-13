package com.smxplore.proto.infrastructure.adapters.out.mongo.repository;

import com.smxplore.proto.infrastructure.adapters.out.mongo.document.SiteDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SiteMongoRepository extends ReactiveMongoRepository<SiteDocument, UUID> {

    Flux<SiteDocument> findAllByZoneId(UUID zoneId);
}
