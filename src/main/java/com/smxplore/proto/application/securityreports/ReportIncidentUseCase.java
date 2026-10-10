package com.smxplore.proto.application.securityreports;

import com.smxplore.proto.domain.exceptions.securityreport.InvalidReportRequestException;
import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.types.UserRef;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ReportIncidentUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReportIncidentUseCase implements ReportIncidentUseCasePort {

    private final SecurityReportRepositoryPort securityReportRepo;

    @Override
    public Mono<SecurityReport> handle(UserRef user, SecurityReport report) {
        user.validate();
        return Mono.justOrEmpty(report)
                .switchIfEmpty(Mono.error(new InvalidReportRequestException("SecurityReport cannot be null")))
                .map(securityReport -> validateAndBuild(report, user))
                .flatMap(securityReportRepo::save);
    }

    private SecurityReport validateAndBuild(SecurityReport report, UserRef user) {
        report.validate();
        return report.toBuilder().user(user).build();
    }
}

