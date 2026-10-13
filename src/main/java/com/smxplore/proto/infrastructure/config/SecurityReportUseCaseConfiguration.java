package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.application.securityreports.AddReportPhotosUseCase;
import com.smxplore.proto.application.securityreports.ChangeReportTextUseCase;
import com.smxplore.proto.application.securityreports.CloseReportUseCase;
import com.smxplore.proto.application.securityreports.CommentReportUseCase;
import com.smxplore.proto.application.securityreports.ConsultOneReportUseCase;
import com.smxplore.proto.application.securityreports.ConsultReportCommentUseCase;
import com.smxplore.proto.application.securityreports.ConsultReportsByUserUseCase;
import com.smxplore.proto.application.securityreports.ConsultReportsUseCase;
import com.smxplore.proto.application.securityreports.MarkReportAsSolvedUseCase;
import com.smxplore.proto.application.securityreports.ReportIncidentUseCase;
import com.smxplore.proto.domain.ports.repository.CommentRepositoryPort;
import com.smxplore.proto.domain.ports.repository.SecurityReportRepositoryPort;
import com.smxplore.proto.domain.ports.usecases.securityreport.AddReportPhotosUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ChangeReportTextUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.CloseReportUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.CommentReportUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultOneReportUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultReportCommentsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultReportsByUserUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ConsultReportsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.MarkReportAsSolvedUseCasePort;
import com.smxplore.proto.domain.ports.usecases.securityreport.ReportIncidentUseCasePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SecurityReportUseCaseConfiguration {

    @Bean
    public ReportIncidentUseCasePort reportIncidentUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new ReportIncidentUseCase(securityReportRepository);
    }

    @Bean
    public ConsultOneReportUseCasePort consultOneReportUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new ConsultOneReportUseCase(securityReportRepository);
    }

    @Bean
    public ConsultReportsUseCasePort consultReportsUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new ConsultReportsUseCase(securityReportRepository);
    }

    @Bean
    public ConsultReportsByUserUseCasePort consultReportsByUserUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new ConsultReportsByUserUseCase(securityReportRepository);
    }

    @Bean
    public ChangeReportTextUseCasePort changeReportTextUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new ChangeReportTextUseCase(securityReportRepository);
    }

    @Bean
    public AddReportPhotosUseCasePort addReportPhotosUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new AddReportPhotosUseCase(securityReportRepository);
    }

    @Bean
    public CloseReportUseCasePort closeReportUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new CloseReportUseCase(securityReportRepository);
    }

    @Bean
    public MarkReportAsSolvedUseCasePort markReportAsSolvedUseCase(SecurityReportRepositoryPort securityReportRepository) {
        return new MarkReportAsSolvedUseCase(securityReportRepository);
    }

    @Bean
    public CommentReportUseCasePort commentReportUseCase(CommentRepositoryPort commentRepository) {
        return new CommentReportUseCase(commentRepository);
    }

    @Bean
    public ConsultReportCommentsUseCasePort consultReportCommentsUseCase(CommentRepositoryPort commentRepository) {
        return new ConsultReportCommentUseCase(commentRepository);
    }
}
