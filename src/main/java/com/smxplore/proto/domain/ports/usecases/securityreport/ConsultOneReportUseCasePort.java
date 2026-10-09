package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;

import java.util.Optional;
import java.util.UUID;

public interface ConsultOneReportUseCasePort {
    Optional<SecurityReport> handle(UUID securityReportId);
}
