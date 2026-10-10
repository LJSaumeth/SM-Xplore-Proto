package com.smxplore.proto.application.securityreports;

import com.smxplore.proto.domain.exceptions.securityreport.ReportNotFoundException;
import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultOneReportUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ConsultOneReportUseCase implements ConsultOneReportUseCasePort {
    private final SecurityReportRepositoryPort securityReportRepo;

    @Override
    public Mono<SecurityReport> handle(UUID securityReportId) {
        return securityReportRepo.findById(securityReportId)
                .switchIfEmpty(Mono.error(new ReportNotFoundException(securityReportId)));
    }
}
