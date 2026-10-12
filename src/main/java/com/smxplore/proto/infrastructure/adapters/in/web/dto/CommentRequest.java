package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @Valid @NotNull UserRefDto user,
        @NotBlank String content
) {
}
