package com.smxplore.proto.application.securityreports;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultReportsUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ConsultReportsUseCase implements ConsultReportsUseCasePort {

    private final SecurityReportRepositoryPort securityReportRepo;

    @Override
    public Flux<SecurityReport> handle(SecurityReportStatus status) {
        return securityReportRepo.findAllByStatus(status);
    }
}
