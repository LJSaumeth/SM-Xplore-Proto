package com.smxplore.proto.domain.model.tourist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.exceptions.UserAccountNotActiveException;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

class TouristTest {

    private Tourist buildTourist() {
        return new Tourist(
                UUID.randomUUID(),
                "John Doe",
                "john.doe@example.com",
                "+573001234567",
                "hashed-password",
                UserStatus.ACTIVE,
                Instant.now(),
                "123456789",
                DniType.CITIZENSHIP_CARD,
                "es",
                null,
                "Colombian");
    }

    @Test
    void shouldBuildTouristWithTouristRole() {
        Tourist tourist = buildTourist();

        assertEquals(UserRole.TOURIST, tourist.getRole());
        assertEquals("john.doe@example.com", tourist.getEmail());
    }

    @Test
    void shouldNormalizeEmailToLowerCase() {
        Tourist tourist = new Tourist(
                UUID.randomUUID(),
                "John Doe",
                "John.Doe@Example.COM",
                "+573001234567",
                "hashed-password",
                UserStatus.ACTIVE,
                Instant.now(),
                "123456789",
                DniType.CITIZENSHIP_CARD,
                null,
                null,
                null);

        assertEquals("john.doe@example.com", tourist.getEmail());
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(InvalidUserDataException.class, () -> new Tourist(
                UUID.randomUUID(),
                "John Doe",
                "not-an-email",
                "+573001234567",
                "hashed-password",
                UserStatus.ACTIVE,
                Instant.now(),
                "123456789",
                DniType.CITIZENSHIP_CARD,
                null,
                null,
                null));
    }

    @Test
    void shouldRejectBlankFullName() {
        assertThrows(InvalidUserDataException.class, () -> new Tourist(
                UUID.randomUUID(),
                " ",
                "john.doe@example.com",
                "+573001234567",
                "hashed-password",
                UserStatus.ACTIVE,
                Instant.now(),
                "123456789",
                DniType.CITIZENSHIP_CARD,
                null,
                null,
                null));
    }

    @Test
    void shouldUpdateProfile() {
        Tourist tourist = buildTourist();

        tourist.updateProfile("Jane Doe", "jane.doe@example.com", "+573009876543");

        assertEquals("Jane Doe", tourist.getFullName());
        assertEquals("jane.doe@example.com", tourist.getEmail());
        assertEquals("+573009876543", tourist.getPhone());
    }

    @Test
    void shouldBlockAndActivateAccount() {
        Tourist tourist = buildTourist();

        tourist.block();
        assertFalse(tourist.isActive());
        assertEquals(UserStatus.BLOCKED, tourist.getStatus());
        assertThrows(UserAccountNotActiveException.class, tourist::ensureActive);

        tourist.activate();
        assertTrue(tourist.isActive());
    }
}
