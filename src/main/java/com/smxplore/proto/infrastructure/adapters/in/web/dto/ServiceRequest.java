package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.service.ServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record ServiceRequest(
        @Valid @NotNull ProviderRefDto provider,
        @NotBlank String name,
        @NotNull ServiceType type,
        @NotNull @DecimalMin(value = "0.0") BigDecimal price,
        @Valid @NotNull LocationDto location,
        List<String> photosUrls,
        List<AttributeDto> extras
) {
    public ServiceRequest {
        photosUrls = photosUrls == null
                ? List.of()
                : photosUrls.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(url -> !url.isBlank())
                        .toList();
        extras = extras == null ? List.of() : extras.stream().filter(Objects::nonNull).toList();
    }
}
