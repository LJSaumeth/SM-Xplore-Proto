package com.smxplore.proto.infrastructure.adapters.in.web;

import com.smxplore.proto.domain.exceptions.securityreport.ReportNotFoundException;
import com.smxplore.proto.domain.model.securityreport.Comment;
import com.smxplore.proto.domain.model.securityreport.SecurityReport;
import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.model.types.Location;
import com.smxplore.proto.domain.model.types.UserRef;
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
import com.smxplore.proto.infrastructure.adapters.in.web.controller.SecurityReportController;
import com.smxplore.proto.infrastructure.adapters.in.web.error.GlobalExceptionHandler;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.SecurityReportWebMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = SecurityReportController.class)
@Import({SecurityReportWebMapperImpl.class, GlobalExceptionHandler.class})
class SecurityReportControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReportIncidentUseCasePort reportIncidentUseCase;

    @MockitoBean
    private ConsultOneReportUseCasePort consultOneReportUseCase;

    @MockitoBean
    private ConsultReportsUseCasePort consultReportsUseCase;

    @MockitoBean
    private ConsultReportsByUserUseCasePort consultReportsByUserUseCase;

    @MockitoBean
    private ChangeReportTextUseCasePort changeReportTextUseCase;

    @MockitoBean
    private AddReportPhotosUseCasePort addReportPhotosUseCase;

    @MockitoBean
    private CloseReportUseCasePort closeReportUseCase;

    @MockitoBean
    private MarkReportAsSolvedUseCasePort markReportAsSolvedUseCase;

    @MockitoBean
    private CommentReportUseCasePort commentReportUseCase;

    @MockitoBean
    private ConsultReportCommentsUseCasePort consultReportCommentsUseCase;

    @Autowired
    void configureClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .jwt(jwt -> jwt.subject("11111111-1111-1111-1111-111111111111")
                                .claim("name", "Token User").claim("role", "TOURIST")))
                .mutateWith(SecurityMockServerConfigurers.csrf());
    }

    @Test
    void reportIncidentMapsRequestAndReturnsCreatedReport() {
        var user = new UserRef(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Token User");
        var reported = SecurityReport.builder()
                .user(user)
                .text("Robo en el parque")
                .location(new Location(4.6, -74.1))
                .build();

        given(reportIncidentUseCase.handle(any(), argThat(report ->
                "Robo en el parque".equals(report.getText())
                        && new Location(4.6, -74.1).equals(report.getLocation())
                        && user.equals(report.getUser()))))
                .willReturn(Mono.just(reported));

        webTestClient.post().uri("/api/v1/security-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "user": {"id": "%s", "name": "Nestor"},
                          "text": "Robo en el parque",
                          "location": {"latitude": 4.6, "longitude": -74.1},
                          "photosUrls": ["http://photo1"]
                        }
                        """.formatted(user.id()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(reported.getId().toString())
                .jsonPath("$.text").isEqualTo("Robo en el parque")
                .jsonPath("$.status").isEqualTo(SecurityReportStatus.ONGOING.name())
                .jsonPath("$.user.name").isEqualTo("Token User");
    }

    @Test
    void reportIncidentRejectsInvalidRequest() {
        webTestClient.post().uri("/api/v1/security-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "user": {"id": "%s", "name": "Nestor"},
                          "text": "   "
                        }
                        """.formatted(UUID.randomUUID()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void findOneReturnsNotFoundWhenTheReportDoesNotExist() {
        var reportId = UUID.randomUUID();
        given(consultOneReportUseCase.handle(reportId))
                .willReturn(Mono.error(new ReportNotFoundException(reportId)));

        webTestClient.get().uri("/api/v1/security-reports/{reportId}", reportId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.path").isEqualTo("/api/v1/security-reports/" + reportId);
    }

    @Test
    void commentUsesTheReportIdOfThePath() {
        var reportId = UUID.randomUUID();
        given(commentReportUseCase.handle(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        webTestClient.post().uri("/api/v1/security-reports/{reportId}/comments", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"user": {"id": "%s", "name": "Pedro"}, "content": "Yo lo vi"}
                        """.formatted(UUID.randomUUID()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.reportId").isEqualTo(reportId.toString())
                .jsonPath("$.content").isEqualTo("Yo lo vi")
                .jsonPath("$.user.name").isEqualTo("Token User");
    }

    @Test
    void findCommentsReturnsTheCommentsOfTheReport() {
        var reportId = UUID.randomUUID();
        var comment = Comment.builder()
                .reportId(reportId)
                .user(new UserRef(UUID.randomUUID(), "Pedro"))
                .content("Yo lo vi")
                .build();
        given(consultReportCommentsUseCase.handle(reportId)).willReturn(Flux.just(comment));

        webTestClient.get().uri("/api/v1/security-reports/{reportId}/comments", reportId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(comment.getId().toString())
                .jsonPath("$[0].content").isEqualTo("Yo lo vi");
    }
}
