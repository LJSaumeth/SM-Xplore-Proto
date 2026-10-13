package com.smxplore.proto.application.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

class BlockOrSuspendUserUseCaseTest {

    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final BlockOrSuspendUserUseCase useCase = new BlockOrSuspendUserUseCase(userRepository);

    private Tourist tourist(UserStatus status) {
        return Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("+573001234567")
                .passwordHash("hashed")
                .role(UserRole.TOURIST)
                .status(status)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();
    }

    @Test
    void shouldBlockAnAccount() {
        var user = tourist(UserStatus.ACTIVE);
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.handle(user.getId(), UserStatus.BLOCKED))
                .assertNext(result -> assertThat(result.getStatus()).isEqualTo(UserStatus.BLOCKED))
                .verifyComplete();

        verify(userRepository).save(user);
    }

    @Test
    void shouldSuspendAnAccount() {
        var user = tourist(UserStatus.ACTIVE);
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.handle(user.getId(), UserStatus.SUSPENDED))
                .assertNext(result -> assertThat(result.getStatus()).isEqualTo(UserStatus.SUSPENDED))
                .verifyComplete();
    }

    @Test
    void shouldReactivateAnAccount() {
        var user = tourist(UserStatus.BLOCKED);
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.handle(user.getId(), UserStatus.ACTIVE))
                .assertNext(result -> assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE))
                .verifyComplete();
    }

    @Test
    void shouldRejectStatusesOtherThanActiveBlockedOrSuspended() {
        StepVerifier.create(useCase.handle(UUID.randomUUID(), UserStatus.INACTIVE))
                .expectError(InvalidUserDataException.class)
                .verify();
    }

    @Test
    void shouldRejectDirectActivationOfInactiveAccounts() {
        var user = tourist(UserStatus.INACTIVE);
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.handle(user.getId(), UserStatus.ACTIVE))
                .expectError(InvalidUserDataException.class)
                .verify();
    }
}
