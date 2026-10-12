package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChangeReportTextUseCasePort {
    Mono<SecurityReport> handle(UUID reportId, String newText);
}
