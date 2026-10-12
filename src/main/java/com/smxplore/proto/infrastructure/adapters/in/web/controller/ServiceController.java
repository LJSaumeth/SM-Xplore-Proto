package com.smxplore.proto.infrastructure.adapters.in.web.controller;

import com.smxplore.proto.domain.model.service.ServiceStatus;
import com.smxplore.proto.domain.model.service.ServiceType;
import com.smxplore.proto.domain.ports.usecases.service.AddAttributeToServiceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.AddServicePhotosUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.ChangeServicePriceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServiceUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByProviderUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByStatusUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.FindServicesByTypeUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.MarkServiceAsAvailableUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.MarkServiceAsUnavailableUseCasePort;
import com.smxplore.proto.domain.ports.usecases.service.RegisterServiceUseCasePort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.AddAttributesRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ChangePriceRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.PhotosRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ServiceRequest;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.ServiceResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.mapper.ServiceWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Services offered by providers")
public class ServiceController {

    private final RegisterServiceUseCasePort registerServiceUseCase;
    private final FindServiceUseCasePort findServiceUseCase;
    private final FindServicesByStatusUseCasePort findServicesByStatusUseCase;
    private final FindServicesByTypeUseCasePort findServicesByTypeUseCase;
    private final FindServicesByProviderUseCasePort findServicesByProviderUseCase;
    private final ChangeServicePriceUseCasePort changeServicePriceUseCase;
    private final MarkServiceAsAvailableUseCasePort markServiceAsAvailableUseCase;
    private final MarkServiceAsUnavailableUseCasePort markServiceAsUnavailableUseCase;
    private final AddServicePhotosUseCasePort addServicePhotosUseCase;
    private final AddAttributeToServiceUseCasePort addAttributeToServiceUseCase;
    private final ServiceWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a service for a provider")
    public Mono<ServiceResponse> register(@Valid @RequestBody ServiceRequest request) {
        var provider = mapper.toDomain(request.provider());
        return registerServiceUseCase.handle(provider, mapper.toDomain(request))
                .map(mapper::toResponse);
    }

    @GetMapping("/{serviceId}")
    @Operation(summary = "Get one service by id")
    public Mono<ServiceResponse> findOne(@PathVariable UUID serviceId) {
        return findServiceUseCase.handle(serviceId).map(mapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "List services by status")
    public Flux<ServiceResponse> findByStatus(@RequestParam ServiceStatus status) {
        return findServicesByStatusUseCase.handle(status).map(mapper::toResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "List services by type and status")
    public Flux<ServiceResponse> findByType(@RequestParam ServiceType type,
                                            @RequestParam ServiceStatus status) {
        return findServicesByTypeUseCase.handle(type, status).map(mapper::toResponse);
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "List the services of a provider")
    public Flux<ServiceResponse> findByProvider(@PathVariable UUID providerId) {
        return findServicesByProviderUseCase.handle(providerId).map(mapper::toResponse);
    }

    @PatchMapping("/{serviceId}/price")
    @Operation(summary = "Change the price of a service")
    public Mono<ServiceResponse> changePrice(@PathVariable UUID serviceId,
                                             @Valid @RequestBody ChangePriceRequest request) {
        return changeServicePriceUseCase.handle(serviceId, request.price()).map(mapper::toResponse);
    }

    @PutMapping("/{serviceId}/available")
    @Operation(summary = "Mark a service as available")
    public Mono<ServiceResponse> markAsAvailable(@PathVariable UUID serviceId) {
        return markServiceAsAvailableUseCase.handle(serviceId).map(mapper::toResponse);
    }

    @PutMapping("/{serviceId}/unavailable")
    @Operation(summary = "Mark a service as unavailable")
    public Mono<ServiceResponse> markAsUnavailable(@PathVariable UUID serviceId) {
        return markServiceAsUnavailableUseCase.handle(serviceId).map(mapper::toResponse);
    }

    @PostMapping("/{serviceId}/photos")
    @Operation(summary = "Add photos to a service")
    public Mono<ServiceResponse> addPhotos(@PathVariable UUID serviceId,
                                           @Valid @RequestBody PhotosRequest request) {
        return addServicePhotosUseCase.handle(serviceId, request.photosUrls()).map(mapper::toResponse);
    }

    @PostMapping("/{serviceId}/attributes")
    @Operation(summary = "Add extra attributes to a service")
    public Mono<ServiceResponse> addAttributes(@PathVariable UUID serviceId,
                                               @Valid @RequestBody AddAttributesRequest request) {
        return addAttributeToServiceUseCase.handle(serviceId, mapper.toAttributeList(request.attributes()))
                .map(mapper::toResponse);
    }
}
