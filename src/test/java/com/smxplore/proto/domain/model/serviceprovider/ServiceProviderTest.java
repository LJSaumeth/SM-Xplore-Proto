package com.smxplore.proto.domain.model.serviceprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smxplore.proto.domain.exceptions.ServiceProviderRegistrationNotPendingException;
import com.smxplore.proto.domain.model.user.UserStatus;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

class ServiceProviderTest {

    private ServiceProvider buildProvider(RegistrationStatus registrationStatus) {
        return new ServiceProvider(
                UUID.randomUUID(),
                "Kayak Tours SAS",
                "contact@kayaktours.com",
                "+573001112233",
                "hashed-password",
                UserStatus.INACTIVE,
                Instant.now(),
                "900123456",
                "Nautical",
                registrationStatus,
                11.24,
                -74.19,
                null);
    }

    @Test
    void shouldApprovePendingRegistration() {
        ServiceProvider provider = buildProvider(RegistrationStatus.PENDING);

        provider.approveRegistration();

        assertEquals(RegistrationStatus.APPROVED, provider.getRegistrationStatus());
        assertTrue(provider.isRegistrationApproved());
        assertTrue(provider.isActive());
    }

    @Test
    void shouldRejectPendingRegistration() {
        ServiceProvider provider = buildProvider(RegistrationStatus.PENDING);

        provider.rejectRegistration();

        assertEquals(RegistrationStatus.REJECTED, provider.getRegistrationStatus());
        assertFalse(provider.isRegistrationApproved());
    }

    @Test
    void shouldNotApproveRegistrationThatIsNotPending() {
        ServiceProvider provider = buildProvider(RegistrationStatus.APPROVED);

        assertThrows(ServiceProviderRegistrationNotPendingException.class, provider::approveRegistration);
    }
}
