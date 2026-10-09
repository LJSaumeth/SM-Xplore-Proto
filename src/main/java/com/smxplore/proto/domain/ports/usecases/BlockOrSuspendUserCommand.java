package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.model.user.UserStatus;

import java.util.UUID;

public record BlockOrSuspendUserCommand(
        UUID userId,
        UserStatus targetStatus) {

    public BlockOrSuspendUserCommand {
        if (targetStatus != UserStatus.BLOCKED && targetStatus != UserStatus.SUSPENDED) {
            throw new InvalidUserDataException(
                    "The target status must be either BLOCKED or SUSPENDED.");
        }
    }
}
