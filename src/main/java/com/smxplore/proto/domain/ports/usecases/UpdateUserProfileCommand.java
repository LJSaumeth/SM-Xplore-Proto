package com.smxplore.proto.domain.ports.usecases;

import java.util.UUID;

public record UpdateUserProfileCommand(
        UUID userId,
        String fullName,
        String email,
        String phone) {
}
