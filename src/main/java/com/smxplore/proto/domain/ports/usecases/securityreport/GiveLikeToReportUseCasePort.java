package com.smxplore.proto.domain.ports.usecases.securityreport;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GiveLikeToReportUseCasePort {
    Mono<Void> handle(UUID reportId, boolean remove);
}
