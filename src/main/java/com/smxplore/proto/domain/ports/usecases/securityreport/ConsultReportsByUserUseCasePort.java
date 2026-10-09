package com.smxplore.proto.domain.ports.usecases.securityreport;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;

import java.util.List;
import java.util.UUID;

public interface ConsultReportsByUserUseCasePort {
    List<SecurityReport> handle(UUID userId);
}
