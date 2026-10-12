package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.types.UserRef;
import reactor.core.publisher.Mono;

public interface ReportIncidentUseCasePort {
    Mono<SecurityReport> handle(UserRef user, SecurityReport report);
}
