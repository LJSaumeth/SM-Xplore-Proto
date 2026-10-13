package com.smxplore.proto.application.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.InvalidUserRoleException;
import com.smxplore.proto.domain.exceptions.UserNotFoundException;
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

class AssignUserRoleUseCaseTest {

    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final AssignUserRoleUseCase useCase = new AssignUserRoleUseCase(userRepository);

    private Tourist tourist() {
        return Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("+573001234567")
                .passwordHash("hashed")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();
    }

    @Test
    void shouldFailWhenTheUserDoesNotExist() {
        var id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.handle(id, UserRole.GUIDE))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnTheUserWhenTheRoleDoesNotChange() {
        var user = tourist();
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.handle(user.getId(), UserRole.TOURIST))
                .assertNext(result -> assertThat(result).isSameAs(user))
                .verifyComplete();
    }

    @Test
    void shouldRejectRoleChanges() {
        var user = tourist();
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.handle(user.getId(), UserRole.GUIDE))
                .expectError(InvalidUserRoleException.class)
                .verify();
    }
}
