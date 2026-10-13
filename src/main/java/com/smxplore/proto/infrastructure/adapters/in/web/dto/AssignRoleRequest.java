package com.smxplore.proto.infrastructure.adapters.in.web.dto;

import com.smxplore.proto.domain.model.user.UserRole;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
        @NotNull UserRole role
) {
}
