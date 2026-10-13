package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.ports.usecases.zone.FindZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.ListZonesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.RegisterZoneUseCasePort;
import com.smxplore.proto.domain.ports.usecases.zone.RenameZoneUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ZoneRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ZoneResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ZoneWebMapper;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/zones")
@RequiredArgsConstructor
@Tag(name = "Zones", description = "Tourist zones that group the sites")
public class ZoneController {

    private final RegisterZoneUseCasePort registerZoneUseCase;
    private final RenameZoneUseCasePort renameZoneUseCase;
    private final FindZoneUseCasePort findZoneUseCase;
    private final ListZonesUseCasePort listZonesUseCase;
    private final ZoneWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a zone")
    public Mono<ZoneResponse> register(@Valid @RequestBody ZoneRequest request) {
        return registerZoneUseCase.handle(request.name()).map(mapper::toResponse);
    }

    @GetMapping("/{zoneId}")
    @Operation(summary = "Get one zone by id")
    public Mono<ZoneResponse> findOne(@PathVariable UUID zoneId) {
        return findZoneUseCase.handle(zoneId).map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "List all zones")
    public Flux<ZoneResponse> findAll() {
        return listZonesUseCase.handle().map(mapper::toResponse);
    }

    @PatchMapping("/{zoneId}")
    @Operation(summary = "Rename a zone")
    public Mono<ZoneResponse> rename(@PathVariable UUID zoneId, @Valid @RequestBody ZoneRequest request) {
        return renameZoneUseCase.handle(zoneId, request.name()).map(mapper::toResponse);
    }
}
