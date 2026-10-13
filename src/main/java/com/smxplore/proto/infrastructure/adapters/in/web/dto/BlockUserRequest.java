package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.model.user.UserStatus;
import jakarta.validation.constraints.NotNull;

public record BlockUserRequest(
        @NotNull UserStatus status
) {
    public BlockUserRequest {
        if (status != UserStatus.ACTIVE && status != UserStatus.BLOCKED && status != UserStatus.SUSPENDED) {
            throw new InvalidUserDataException("Target status must be ACTIVE, BLOCKED or SUSPENDED.");
        }
    }
}
