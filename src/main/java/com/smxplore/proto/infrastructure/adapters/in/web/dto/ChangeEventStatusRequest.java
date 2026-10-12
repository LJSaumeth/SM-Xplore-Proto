package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.event.EventStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeEventStatusRequest(
        @NotNull EventStatus status
) {
}
