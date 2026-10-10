package com.smxplore.proto.application.securityreports;

import com.smxplore.proto.domain.exceptions.securityreport.ReportNotFoundException;
import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.AddReportPhotosUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class AddReportPhotosUseCase implements AddReportPhotosUseCasePort {
    private final SecurityReportRepositoryPort securityReportRepo;

    @Override
    public Mono<SecurityReport> handle(UUID reportId, Collection<String> photos) {
        return securityReportRepo.findById(reportId)
                .switchIfEmpty(Mono.error(new ReportNotFoundException(reportId)))
                .flatMap(report -> {
                    report.addPhotos(photos);
                    return securityReportRepo.save(report);
                });
    }
}
