package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public record SecurityReportRequest(
        @Valid @NotNull UserRefDto user,
        @NotBlank String text,
        @Valid LocationDto location,
        List<String> photosUrls
) {
    public SecurityReportRequest {
        photosUrls = photosUrls == null
                ? List.of()
                : photosUrls.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(url -> !url.isBlank())
                        .toList();
    }
}
