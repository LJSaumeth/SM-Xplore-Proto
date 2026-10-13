package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.ports.usecases.site.FindSiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.ListSitesUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.RegisterSiteUseCasePort;
import com.smxplore.proto.domain.ports.usecases.site.UpdateSiteUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SiteRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.SiteResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UpdateSiteRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.SiteWebMapper;
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
@RequestMapping("/api/v1/sites")
@RequiredArgsConstructor
@Tag(name = "Sites", description = "Tourist sites (attractions) that belong to a zone")
public class SiteController {

    private final RegisterSiteUseCasePort registerSiteUseCase;
    private final UpdateSiteUseCasePort updateSiteUseCase;
    private final FindSiteUseCasePort findSiteUseCase;
    private final ListSitesUseCasePort listSitesUseCase;
    private final SiteWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a site inside an existing zone")
    public Mono<SiteResponse> register(@Valid @RequestBody SiteRequest request) {
        return registerSiteUseCase.handle(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @GetMapping("/{siteId}")
    @Operation(summary = "Get one site by id")
    public Mono<SiteResponse> findOne(@PathVariable UUID siteId) {
        return findSiteUseCase.handle(siteId).map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "List sites, optionally filtered by zone")
    public Flux<SiteResponse> findAll(@RequestParam(required = false) UUID zoneId) {
        return listSitesUseCase.handle(zoneId).map(mapper::toResponse);
    }

    @PatchMapping("/{siteId}")
    @Operation(summary = "Update a site (partial)")
    public Mono<SiteResponse> update(@PathVariable UUID siteId, @Valid @RequestBody UpdateSiteRequest request) {
        return updateSiteUseCase.handle(siteId, request.name(), request.latitude(), request.longitude(),
                request.zoneId(), request.photoUrls()).map(mapper::toResponse);
    }
}
