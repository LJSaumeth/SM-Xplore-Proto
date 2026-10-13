package com.smxplore.proto.infrastructure.adapters.in.web;

import com.smxplore.proto.domain.model.reservation.ReservationStatus;
import com.smxplore.proto.domain.ports.usecases.reservation.CancelReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConfirmReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultOneReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationsByTouristUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.CreateReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RegisterReservationAttendanceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RegisterReservationNoShowUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RejectReservationUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.RequestReservationCancellationUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ReservationRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ReservationResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ReservationWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations")
public class ReservationController {

    private final CreateReservationUseCasePort createReservationUseCase;
    private final ConsultOneReservationUseCasePort consultOneReservationUseCase;
    private final ConsultReservationsByStatusUseCasePort consultReservationsByStatusUseCase;
    private final ConsultReservationsByTouristUseCasePort consultReservationsByTouristUseCase;
    private final ConfirmReservationUseCasePort confirmReservationUseCase;
    private final RejectReservationUseCasePort rejectReservationUseCase;
    private final CancelReservationUseCasePort cancelReservationUseCase;
    private final RequestReservationCancellationUseCasePort requestReservationCancellationUseCase;
    private final RegisterReservationAttendanceUseCasePort registerReservationAttendanceUseCase;
    private final RegisterReservationNoShowUseCasePort registerReservationNoShowUseCase;
    private final ReservationWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a reservation")
    public Mono<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        return createReservationUseCase.handle(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @GetMapping("/{reservationId}")
    @Operation(summary = "Get one reservation by id")
    public Mono<ReservationResponse> findOne(@PathVariable UUID reservationId) {
        return consultOneReservationUseCase.handle(reservationId).map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "List reservations by status")
    public Flux<ReservationResponse> findByStatus(@RequestParam ReservationStatus status,
                                                  @RequestParam(defaultValue = "50") int limit) {
        return consultReservationsByStatusUseCase.handle(status, PaginationLimits.clamp(limit))
                .map(mapper::toResponse);
    }

    @GetMapping("/tourists/{touristId}")
    @Operation(summary = "List the reservations of a tourist")
    public Flux<ReservationResponse> findByTourist(@PathVariable UUID touristId,
                                                   @RequestParam(defaultValue = "50") int limit) {
        return consultReservationsByTouristUseCase.handle(touristId, PaginationLimits.clamp(limit))
                .map(mapper::toResponse);
    }

    @PatchMapping("/{reservationId}/confirm")
    @Operation(summary = "Confirm a pending reservation")
    public Mono<ReservationResponse> confirm(@PathVariable UUID reservationId) {
        return confirmReservationUseCase.handle(reservationId).map(mapper::toResponse);
    }

    @PatchMapping("/{reservationId}/reject")
    @Operation(summary = "Reject a pending reservation")
    public Mono<ReservationResponse> reject(@PathVariable UUID reservationId) {
        return rejectReservationUseCase.handle(reservationId).map(mapper::toResponse);
    }

    @PatchMapping("/{reservationId}/cancel")
    @Operation(summary = "Cancel a reservation")
    public Mono<ReservationResponse> cancel(@PathVariable UUID reservationId) {
        return cancelReservationUseCase.handle(reservationId).map(mapper::toResponse);
    }

    @PatchMapping("/{reservationId}/request-cancellation")
    @Operation(summary = "Request the cancellation of a confirmed reservation")
    public Mono<ReservationResponse> requestCancellation(@PathVariable UUID reservationId) {
        return requestReservationCancellationUseCase.handle(reservationId).map(mapper::toResponse);
    }

    @PatchMapping("/{reservationId}/attendance")
    @Operation(summary = "Register the attendance to a confirmed reservation")
    public Mono<ReservationResponse> registerAttendance(@PathVariable UUID reservationId) {
        return registerReservationAttendanceUseCase.handle(reservationId).map(mapper::toResponse);
    }

    @PatchMapping("/{reservationId}/no-show")
    @Operation(summary = "Register the no show of a confirmed reservation")
    public Mono<ReservationResponse> registerNoShow(@PathVariable UUID reservationId) {
        return registerReservationNoShowUseCase.handle(reservationId).map(mapper::toResponse);
    }
}
