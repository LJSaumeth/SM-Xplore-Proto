package com.smxplore.proto.infrastructure.adapters.out.mongo.repository;

import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.infrastructure.adapters.out.mongo.document.SecurityReportDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SecurityReportMongoRepository extends ReactiveMongoRepository<SecurityReportDocument, UUID> {

    Flux<SecurityReportDocument> findAllByStatus(SecurityReportStatus status);

    Flux<SecurityReportDocument> findAllByUser_Id(UUID userId);
}
