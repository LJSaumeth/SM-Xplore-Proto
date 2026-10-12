package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeReportTextRequest(
        @NotBlank String text
) {
}
