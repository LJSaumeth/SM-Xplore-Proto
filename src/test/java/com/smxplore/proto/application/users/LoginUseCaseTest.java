package com.smxplore.proto.application.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.exceptions.InvalidCredentialsException;
import com.smxplore.proto.domain.exceptions.UserAccountNotActiveException;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import com.smxplore.proto.domain.ports.security.TokenIssuerPort;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

class LoginUseCaseTest {

    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final PasswordHasherPort passwordHasher = mock(PasswordHasherPort.class);
    private final TokenIssuerPort tokenIssuer = mock(TokenIssuerPort.class);
    private final LoginUseCase useCase = new LoginUseCase(userRepository, passwordHasher, tokenIssuer);

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
    void shouldIssueTokenWhenCredentialsAreValid() {
        var user = tourist(UserStatus.ACTIVE);
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(user));
        when(passwordHasher.matches("plain", "hashed")).thenReturn(Mono.just(true));
        when(tokenIssuer.issue(user)).thenReturn(Mono.just(new TokenIssuerPort.IssuedToken("jwt-token", 3600)));

        StepVerifier.create(useCase.handle("john.doe@example.com", "plain"))
                .assertNext(result -> {
                    assertEquals(user, result.user());
                    assertEquals("jwt-token", result.token());
                    assertEquals(3600, result.expiresInSeconds());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.handle("nobody@example.com", "plain"))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(tokenIssuer, never()).issue(any());
    }

    @Test
    void shouldFailWhenPasswordDoesNotMatch() {
        var user = tourist(UserStatus.ACTIVE);
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.handle("john.doe@example.com", "wrong"))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(tokenIssuer, never()).issue(any());
    }

    @Test
    void shouldFailWhenAccountIsNotActive() {
        var user = tourist(UserStatus.BLOCKED);
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.handle("john.doe@example.com", "plain"))
                .expectError(UserAccountNotActiveException.class)
                .verify();

        verify(tokenIssuer, never()).issue(any());
    }

    @Test
    void shouldNormalizeEmailWhenLoggingIn() {
        var user = tourist(UserStatus.ACTIVE);
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(Mono.just(true));
        when(tokenIssuer.issue(user)).thenReturn(Mono.just(new TokenIssuerPort.IssuedToken("jwt-token", 3600)));

        StepVerifier.create(useCase.handle("  John.Doe@Example.Com ", "plain"))
                .assertNext(result -> assertEquals("jwt-token", result.token()))
                .verifyComplete();

        verify(tokenIssuer).issue(user);
    }
}
