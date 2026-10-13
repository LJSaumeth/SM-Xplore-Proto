package com.smxplore.proto.application.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.DestinationManagerRegistrationNotPendingException;
import com.smxplore.proto.domain.exceptions.UserAccountNotActiveException;
import com.smxplore.proto.domain.exceptions.UserNotFoundException;
import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.DestinationManagerRepositoryPort;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

class ApproveDestinationManagerRegistrationUseCaseTest {

    private final DestinationManagerRepositoryPort destinationManagerRepository =
            mock(DestinationManagerRepositoryPort.class);
    private final ApproveDestinationManagerRegistrationUseCase useCase =
            new ApproveDestinationManagerRegistrationUseCase(destinationManagerRepository);

    private DestinationManager manager(UserStatus status) {
        return DestinationManager.builder()
                .id(UUID.randomUUID())
                .fullName("Pending Manager")
                .email("manager@example.com")
                .phone("+573001112244")
                .role(UserRole.DESTINATION_MANAGER)
                .status(status)
                .signedUpAt(Instant.now())
                .nit("NIT-123")
                .build();
    }

    @Test
    void shouldApprovePendingRegistrationAndActivateTheAccount() {
        var manager = manager(UserStatus.INACTIVE);
        when(destinationManagerRepository.findById(manager.getId())).thenReturn(Mono.just(manager));
        when(destinationManagerRepository.save(manager)).thenReturn(Mono.just(manager));

        StepVerifier.create(useCase.handle(manager.getId()))
                .assertNext(approved -> assertThat(approved.getStatus()).isEqualTo(UserStatus.ACTIVE))
                .verifyComplete();

        verify(destinationManagerRepository).save(manager);
    }

    @Test
    void shouldFailWhenTheManagerDoesNotExist() {
        var id = UUID.randomUUID();
        when(destinationManagerRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.handle(id))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(destinationManagerRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenTheRegistrationIsNotPending() {
        var manager = manager(UserStatus.ACTIVE);
        when(destinationManagerRepository.findById(manager.getId())).thenReturn(Mono.just(manager));

        StepVerifier.create(useCase.handle(manager.getId()))
                .expectError(DestinationManagerRegistrationNotPendingException.class)
                .verify();

        verify(destinationManagerRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenTheAccountIsBlockedOrSuspended() {
        var manager = manager(UserStatus.BLOCKED);
        when(destinationManagerRepository.findById(manager.getId())).thenReturn(Mono.just(manager));

        StepVerifier.create(useCase.handle(manager.getId()))
                .expectError(UserAccountNotActiveException.class)
                .verify();

        verify(destinationManagerRepository, never()).save(any());
    }
}
