package com.smxplore.proto.application.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.UserEmailAlreadyRegisteredException;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

class RegisterUserUseCaseTest {

    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final PasswordHasherPort passwordHasher = mock(PasswordHasherPort.class);
    private final RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, passwordHasher);

    private Tourist tourist() {
        return Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("John.Doe@Example.com")
                .phone("+573001234567")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("123456789")
                .dniType(DniType.CITIZENSHIP_CARD)
                .build();
    }

    @Test
    void shouldHashPasswordAndSaveWhenEmailAndPhoneAreUnique() {
        var user = tourist();
        when(passwordHasher.hash("plain")).thenReturn(Mono.just("hashed"));
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(userRepository.existsByPhone(anyString())).thenReturn(Mono.just(false));
        when(userRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.handle(user, "plain"))
                .assertNext(saved -> {
                    assertEquals("hashed", saved.getPasswordHash());
                    assertEquals("john.doe@example.com", saved.getEmail());
                })
                .verifyComplete();

        verify(userRepository).save(user);
    }

    @Test
    void shouldFailWhenEmailIsAlreadyRegistered() {
        var user = tourist();
        when(passwordHasher.hash("plain")).thenReturn(Mono.just("hashed"));
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.handle(user, "plain"))
                .expectError(UserEmailAlreadyRegisteredException.class)
                .verify();
    }
}
