package com.smxplore.proto.application.securityreports;

import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultReportsByUserUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class ConsultReportsByUserUseCase implements ConsultReportsByUserUseCasePort {
    private final SecurityReportRepositoryPort securityReportRepo;

    @Override
    public Flux<SecurityReport> handle(UUID userId) {
        return securityReportRepo.findAllByUserId(userId);
    }


}
