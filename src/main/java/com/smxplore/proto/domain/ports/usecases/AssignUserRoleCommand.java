package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.UserRole;

import java.util.UUID;

public record AssignUserRoleCommand(
        UUID userId,
        UserRole role) {
}
