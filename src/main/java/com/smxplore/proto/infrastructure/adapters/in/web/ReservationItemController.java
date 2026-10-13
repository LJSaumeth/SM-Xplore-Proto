package com.smxplore.proto.infrastructure.adapters.in.web;

import com.smxplore.proto.domain.model.reservation.ReservationItemType;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.AddReservationItemsUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultOneReservationItemUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationItemsByTypeUseCasePort;
import com.smxplore.proto.domain.ports.usecases.reservation.ConsultReservationItemsUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ReservationItemRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ReservationItemResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ReservationItemsRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ReservationItemWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Reservation Items")
public class ReservationItemController {

    private final AddReservationItemUseCasePort addReservationItemUseCase;
    private final AddReservationItemsUseCasePort addReservationItemsUseCase;
    private final ConsultReservationItemsUseCasePort consultReservationItemsUseCase;
    private final ConsultOneReservationItemUseCasePort consultOneReservationItemUseCase;
    private final ConsultReservationItemsByTypeUseCasePort consultReservationItemsByTypeUseCase;
    private final ReservationItemWebMapper mapper;

    @PostMapping("/reservations/{reservationId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add one item to a reservation")
    public Mono<ReservationItemResponse> addItem(@PathVariable UUID reservationId,
                                                 @Valid @RequestBody ReservationItemRequest request) {
        return addReservationItemUseCase.handle(reservationId, mapper.toDomain(request))
                .map(mapper::toResponse);
    }

    @PostMapping("/reservations/{reservationId}/items/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add several items of the same type to a reservation")
    public Flux<ReservationItemResponse> addItems(@PathVariable UUID reservationId,
                                                  @Valid @RequestBody ReservationItemsRequest request) {
        return addReservationItemsUseCase.handle(reservationId, mapper.toDomainList(request.items()))
                .map(mapper::toResponse);
    }

    @GetMapping("/reservations/{reservationId}/items")
    @Operation(summary = "List the items of a reservation")
    public Flux<ReservationItemResponse> findByReservation(@PathVariable UUID reservationId) {
        return consultReservationItemsUseCase.handle(reservationId).map(mapper::toResponse);
    }

    @GetMapping("/reservation-items/{reservationItemId}")
    @Operation(summary = "Get one reservation item by id")
    public Mono<ReservationItemResponse> findOne(@PathVariable UUID reservationItemId) {
        return consultOneReservationItemUseCase.handle(reservationItemId).map(mapper::toResponse);
    }

    @GetMapping("/reservation-items")
    @Operation(summary = "List reservation items by type")
    public Flux<ReservationItemResponse> findByType(@RequestParam ReservationItemType type,
                                                    @RequestParam(defaultValue = "50") int limit) {
        return consultReservationItemsByTypeUseCase.handle(type, PaginationLimits.clamp(limit))
                .map(mapper::toResponse);
    }
}
