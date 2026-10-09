package com.smxplore.proto.domain.ports.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.model.user.UserStatus;

import org.junit.jupiter.api.Test;

import java.util.UUID;

class BlockOrSuspendUserCommandTest {

    @Test
    void shouldRejectStatusesOtherThanBlockedOrSuspended() {
        assertThrows(
                InvalidUserDataException.class,
                () -> new BlockOrSuspendUserCommand(UUID.randomUUID(), UserStatus.ACTIVE));
    }

    @Test
    void shouldAcceptBlockedStatus() {
        BlockOrSuspendUserCommand command =
                assertDoesNotThrow(() -> new BlockOrSuspendUserCommand(
                        UUID.randomUUID(), UserStatus.BLOCKED));

        assertEquals(UserStatus.BLOCKED, command.targetStatus());
    }

    @Test
    void shouldAcceptSuspendedStatus() {
        BlockOrSuspendUserCommand command =
                assertDoesNotThrow(() -> new BlockOrSuspendUserCommand(
                        UUID.randomUUID(), UserStatus.SUSPENDED));

        assertEquals(UserStatus.SUSPENDED, command.targetStatus());
    }
}
