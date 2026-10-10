package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SecurityReportRepositoryPort {
    Mono<SecurityReport> save(SecurityReport securityReport);

    Mono<SecurityReport> findById(UUID id);

    Flux<SecurityReport> findAllByStatus(SecurityReportStatus status);

    Flux<SecurityReport> findAll();

    void changeText(UUID reportId, String content);

    void changeStatus(UUID reportId, SecurityReportStatus status);

    Flux<SecurityReport> findAllByUserId(UUID userId);
}
