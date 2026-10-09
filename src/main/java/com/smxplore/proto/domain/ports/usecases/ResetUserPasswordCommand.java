package com.smxplore.proto.domain.ports.usecases;

import java.util.UUID;

public record ResetUserPasswordCommand(
        UUID userId,
        String newPassword) {
}
