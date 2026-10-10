package com.smxplore.proto.domain.model.tourist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.exceptions.UserAccountNotActiveException;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

class TouristTest {

    private Tourist validTourist() {
        return Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("+573001234567")
                .passwordHash("hashed-password")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();
    }

    @Test
    void shouldBuildAndValidateTourist() {
        Tourist tourist = validTourist();

        tourist.validate();

        assertEquals(UserRole.TOURIST, tourist.getRole());
        assertEquals("john.doe@example.com", tourist.getEmail());
    }

    @Test
    void shouldNormalizeEmailToLowerCaseOnValidation() {
        Tourist tourist = Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("John.Doe@Example.COM")
                .phone("+573001234567")
                .passwordHash("hashed-password")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();

        tourist.validate();

        assertEquals("john.doe@example.com", tourist.getEmail());
    }

    @Test
    void shouldRejectInvalidEmail() {
        Tourist tourist = Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("not-an-email")
                .phone("+573001234567")
                .passwordHash("hashed-password")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();

        assertThrows(InvalidUserDataException.class, tourist::validate);
    }

    @Test
    void shouldRejectBlankFullName() {
        Tourist tourist = Tourist.builder()
                .id(UUID.randomUUID())
                .fullName(" ")
                .email("john.doe@example.com")
                .phone("+573001234567")
                .passwordHash("hashed-password")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();

        assertThrows(InvalidUserDataException.class, tourist::validate);
    }

    @Test
    void shouldRejectRoleThatDoesNotMatchTheSubtype() {
        Tourist tourist = Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("+573001234567")
                .passwordHash("hashed-password")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();

        assertThrows(InvalidUserRoleException.class, tourist::validate);
    }

    @Test
    void shouldUpdateProfile() {
        Tourist tourist = validTourist();
        tourist.validate();

        tourist.updateProfile("Jane Doe", "jane.doe@example.com", "+573009876543");

        assertEquals("Jane Doe", tourist.getFullName());
        assertEquals("jane.doe@example.com", tourist.getEmail());
        assertEquals("+573009876543", tourist.getPhone());
    }

    @Test
    void shouldBlockAndActivateAccount() {
        Tourist tourist = validTourist();
        tourist.validate();

        tourist.block();
        assertFalse(tourist.isActive());
        assertEquals(UserStatus.BLOCKED, tourist.getStatus());
        assertThrows(UserAccountNotActiveException.class, tourist::ensureActive);

        tourist.activate();
        assertTrue(tourist.isActive());
    }
}
