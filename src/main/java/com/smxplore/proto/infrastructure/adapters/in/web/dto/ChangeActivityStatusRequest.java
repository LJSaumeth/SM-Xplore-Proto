package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.activity.ActivityStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeActivityStatusRequest(
        @NotNull ActivityStatus status
) {
}
