package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProviderRefDto(
        @NotNull UUID id,
        @NotBlank String name
) {
}
