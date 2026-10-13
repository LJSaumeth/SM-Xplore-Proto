package com.smxplore.proto.application.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.ServiceProviderRegistrationNotPendingException;
import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.serviceprovider.RegistrationStatus;
import com.smxplore.proto.domain.model.serviceprovider.ServiceProvider;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.ServiceProviderRepositoryPort;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

class ApproveServiceProviderRegistrationUseCaseTest {

    private final ServiceProviderRepositoryPort serviceProviderRepository = mock(ServiceProviderRepositoryPort.class);
    private final ApproveServiceProviderRegistrationUseCase useCase =
            new ApproveServiceProviderRegistrationUseCase(serviceProviderRepository);

    private ServiceProvider provider(RegistrationStatus registrationStatus, UserStatus status) {
        return ServiceProvider.builder()
                .id(UUID.randomUUID())
                .fullName("Pending Provider")
                .email("provider@example.com")
                .phone("+573001112233")
                .role(UserRole.SERVICE_PROVIDER)
                .status(status)
                .signedUpAt(Instant.now())
                .nit("NIT-123")
                .registrationStatus(registrationStatus)
                .build();
    }

    @Test
    void shouldApprovePendingRegistrationAndActivateTheAccount() {
        var provider = provider(RegistrationStatus.PENDING, UserStatus.INACTIVE);
        when(serviceProviderRepository.findById(provider.getId())).thenReturn(Mono.just(provider));
        when(serviceProviderRepository.save(provider)).thenReturn(Mono.just(provider));

        StepVerifier.create(useCase.handle(provider.getId()))
                .assertNext(approved -> {
                    assertThat(approved.getRegistrationStatus()).isEqualTo(RegistrationStatus.APPROVED);
                    assertThat(approved.getStatus()).isEqualTo(UserStatus.ACTIVE);
                })
                .verifyComplete();

        verify(serviceProviderRepository).save(provider);
    }

    @Test
    void shouldFailWhenTheProviderDoesNotExist() {
        var id = UUID.randomUUID();
        when(serviceProviderRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.handle(id))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(serviceProviderRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenTheRegistrationIsNotPending() {
        var provider = provider(RegistrationStatus.APPROVED, UserStatus.ACTIVE);
        when(serviceProviderRepository.findById(provider.getId())).thenReturn(Mono.just(provider));

        StepVerifier.create(useCase.handle(provider.getId()))
                .expectError(ServiceProviderRegistrationNotPendingException.class)
                .verify();

        verify(serviceProviderRepository, never()).save(any());
    }
}
