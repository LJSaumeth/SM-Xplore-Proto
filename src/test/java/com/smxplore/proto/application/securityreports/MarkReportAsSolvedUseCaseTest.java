package com.smxplore.proto.application.securityreports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.securityreport.InvalidStatusTransitionException;
import com.smxplore.proto.domain.exceptions.securityreport.ReportNotFoundException;
import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.model.types.UserRef;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

class MarkReportAsSolvedUseCaseTest {

    private final SecurityReportRepositoryPort securityReportRepository = mock(SecurityReportRepositoryPort.class);
    private final MarkReportAsSolvedUseCase useCase = new MarkReportAsSolvedUseCase(securityReportRepository);

    private SecurityReport report(SecurityReportStatus status) {
        return SecurityReport.builder()
                .user(new UserRef(UUID.randomUUID(), "Nestor"))
                .text("Robo en el parque")
                .status(status)
                .build();
    }

    @Test
    void shouldMarkAReportAsSolved() {
        var report = report(SecurityReportStatus.REPORTED);
        when(securityReportRepository.findById(report.getId())).thenReturn(Mono.just(report));
        when(securityReportRepository.save(report)).thenReturn(Mono.just(report));

        StepVerifier.create(useCase.handle(report.getId()))
                .assertNext(solved -> assertThat(solved.getStatus()).isEqualTo(SecurityReportStatus.SOLVED))
                .verifyComplete();

        verify(securityReportRepository).save(report);
    }

    @Test
    void shouldFailWhenTheReportDoesNotExist() {
        var id = UUID.randomUUID();
        when(securityReportRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.handle(id))
                .expectError(ReportNotFoundException.class)
                .verify();

        verify(securityReportRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenTheReportIsAlreadySolved() {
        var report = report(SecurityReportStatus.SOLVED);
        when(securityReportRepository.findById(report.getId())).thenReturn(Mono.just(report));

        StepVerifier.create(useCase.handle(report.getId()))
                .expectError(InvalidStatusTransitionException.class)
                .verify();

        verify(securityReportRepository, never()).save(any());
    }
}
