package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.model.securityreport.SecurityReportStatus;
import com.smxplore.proto.domain.model.types.UserRef;
import com.smxplore.proto.domain.model.user.UserRole;
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
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ChangeReportTextRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.CommentRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.CommentResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.PhotosRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SecurityReportRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SecurityReportResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.SecurityReportWebMapper;
import com.smxplore.proto.infrastructure.config.JwtConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/security-reports")
@RequiredArgsConstructor
@Tag(name = "Security Reports", description = "Security incidents reported by users")
public class SecurityReportController {

    private final ReportIncidentUseCasePort reportIncidentUseCase;
    private final ConsultOneReportUseCasePort consultOneReportUseCase;
    private final ConsultReportsUseCasePort consultReportsUseCase;
    private final ConsultReportsByUserUseCasePort consultReportsByUserUseCase;
    private final ChangeReportTextUseCasePort changeReportTextUseCase;
    private final AddReportPhotosUseCasePort addReportPhotosUseCase;
    private final CloseReportUseCasePort closeReportUseCase;
    private final MarkReportAsSolvedUseCasePort markReportAsSolvedUseCase;
    private final CommentReportUseCasePort commentReportUseCase;
    private final ConsultReportCommentsUseCasePort consultReportCommentsUseCase;
    private final SecurityReportWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Report a security incident (the reporter is taken from the token)")
    public Mono<SecurityReportResponse> reportIncident(@Valid @RequestBody SecurityReportRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        var user = new UserRef(UUID.fromString(jwt.getSubject()), jwt.getClaimAsString("name"));
        var securityReport = mapper.toDomain(request).toBuilder().user(user).build();
        return reportIncidentUseCase.handle(user, securityReport)
                .map(mapper::toResponse);
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "Get one security report by id")
    public Mono<SecurityReportResponse> findOne(@PathVariable UUID reportId) {
        return consultOneReportUseCase.handle(reportId).map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "List security reports by status")
    public Flux<SecurityReportResponse> findByStatus(@RequestParam SecurityReportStatus status) {
        return consultReportsUseCase.handle(status).map(mapper::toResponse);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "List security reports reported by a user")
    public Flux<SecurityReportResponse> findByUser(@PathVariable UUID userId) {
        return consultReportsByUserUseCase.handle(userId).map(mapper::toResponse);
    }

    @PatchMapping("/{reportId}/text")
    @Operation(summary = "Change the text of a security report (its author or staff)")
    public Mono<SecurityReportResponse> changeText(@PathVariable UUID reportId,
                                                   @Valid @RequestBody ChangeReportTextRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        return requireReportAuthorOrStaff(jwt, reportId)
                .then(Mono.defer(() -> changeReportTextUseCase.handle(reportId, request.text())))
                .map(mapper::toResponse);
    }

    @PostMapping("/{reportId}/photos")
    @Operation(summary = "Add photos to a security report (its author or staff)")
    public Mono<SecurityReportResponse> addPhotos(@PathVariable UUID reportId,
                                                  @Valid @RequestBody PhotosRequest request,
                                                  @AuthenticationPrincipal Jwt jwt) {
        return requireReportAuthorOrStaff(jwt, reportId)
                .then(Mono.defer(() -> addReportPhotosUseCase.handle(reportId, request.photosUrls())))
                .map(mapper::toResponse);
    }

    @PostMapping("/{reportId}/close")
    @Operation(summary = "Close a security report (marks it as reported)")
    public Mono<SecurityReportResponse> close(@PathVariable UUID reportId) {
        return closeReportUseCase.handle(reportId).map(mapper::toResponse);
    }

    @PostMapping("/{reportId}/solve")
    @Operation(summary = "Mark a security report as solved")
    public Mono<SecurityReportResponse> solve(@PathVariable UUID reportId) {
        return markReportAsSolvedUseCase.handle(reportId).map(mapper::toResponse);
    }

    @PostMapping("/{reportId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Comment a security report (the author is taken from the token)")
    public Mono<CommentResponse> comment(@PathVariable UUID reportId,
                                         @Valid @RequestBody CommentRequest request,
                                         @AuthenticationPrincipal Jwt jwt) {
        var user = new UserRef(UUID.fromString(jwt.getSubject()), jwt.getClaimAsString("name"));
        var comment = mapper.toDomain(request).toBuilder().reportId(reportId).user(user).build();
        return commentReportUseCase.handle(comment).map(mapper::toResponse);
    }

    @GetMapping("/{reportId}/comments")
    @Operation(summary = "List the comments of a security report")
    public Flux<CommentResponse> findComments(@PathVariable UUID reportId) {
        return consultReportCommentsUseCase.handle(reportId).map(mapper::toResponse);
    }

    private static boolean isAdminOrManager(Jwt jwt) {
        var role = jwt.getClaimAsString(JwtConfiguration.ROLE_CLAIM);
        return UserRole.ADMIN.name().equals(role) || UserRole.DESTINATION_MANAGER.name().equals(role);
    }

    private Mono<Void> requireReportAuthorOrStaff(Jwt jwt, UUID reportId) {
        if (isAdminOrManager(jwt)) {
            return Mono.empty();
        }
        return consultOneReportUseCase.handle(reportId)
                .doOnNext(report -> {
                    var authorId = report.getUser() == null ? null : report.getUser().id();
                    if (authorId == null || !authorId.toString().equals(jwt.getSubject())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Only the author of the report (or staff) can change it.");
                    }
                })
                .then();
    }
}
