package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConsultOneReportUseCasePort {
    Mono<SecurityReport> handle(UUID securityReportId);
}
