package com.smxplore.proto.domain.model.serviceprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smxplore.proto.domain.exceptions.ServiceProviderRegistrationNotPendingException;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

class ServiceProviderTest {

    private ServiceProvider providerWith(RegistrationStatus registrationStatus) {
        return ServiceProvider.builder()
                .id(UUID.randomUUID())
                .fullName("Kayak Tours SAS")
                .email("contact@kayaktours.com")
                .phone("+573001112233")
                .passwordHash("hashed-password")
                .role(UserRole.SERVICE_PROVIDER)
                .status(UserStatus.INACTIVE)
                .signedUpAt(Instant.now())
                .nit("900123456")
                .serviceType("Nautical")
                .registrationStatus(registrationStatus)
                .latitude(11.24)
                .longitude(-74.19)
                .build();
    }

    @Test
    void shouldApprovePendingRegistration() {
        ServiceProvider provider = providerWith(RegistrationStatus.PENDING);
        provider.validate();

        provider.approveRegistration();

        assertEquals(RegistrationStatus.APPROVED, provider.getRegistrationStatus());
        assertTrue(provider.isRegistrationApproved());
        assertTrue(provider.isActive());
    }

    @Test
    void shouldRejectPendingRegistration() {
        ServiceProvider provider = providerWith(RegistrationStatus.PENDING);
        provider.validate();

        provider.rejectRegistration();

        assertEquals(RegistrationStatus.REJECTED, provider.getRegistrationStatus());
        assertFalse(provider.isRegistrationApproved());
    }

    @Test
    void shouldNotApproveRegistrationThatIsNotPending() {
        ServiceProvider provider = providerWith(RegistrationStatus.APPROVED);
        provider.validate();

        assertThrows(ServiceProviderRegistrationNotPendingException.class, provider::approveRegistration);
    }
}
