package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import reactor.core.publisher.Flux;

public interface ConsultReportsUseCasePort {
    Flux<SecurityReport> handle(SecurityReportStatus status);
}
