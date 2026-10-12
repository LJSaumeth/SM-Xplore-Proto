package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * {@code value} accepts simple JSON values (string, number, boolean) or a
 * nested object with shape {@code {"name": ..., "value": ...}}.
 */
public record AttributeDto(
        @NotBlank String name,
        @NotNull Object value
) {
}
