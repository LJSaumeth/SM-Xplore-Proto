package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ConsultReportsByUserUseCasePort {
    Flux<SecurityReport> handle(UUID userId);
}
