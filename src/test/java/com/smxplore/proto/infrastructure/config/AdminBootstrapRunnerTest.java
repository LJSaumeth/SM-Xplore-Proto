package com.smxplore.proto.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.smxplore.proto.domain.model.admin.Admin;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

class AdminBootstrapRunnerTest {

    private static final String EMAIL = "admin@smoke.test";
    private static final String PASSWORD = "Admin123*";

    private final UserRepositoryPort userRepository = mock(UserRepositoryPort.class);
    private final PasswordHasherPort passwordHasher = mock(PasswordHasherPort.class);

    private AdminBootstrapRunner runner(String email, String password) {
        return new AdminBootstrapRunner(userRepository, passwordHasher, email, password,
                "000000000", DniType.CITIZENSHIP_CARD);
    }

    @Test
    void skipsWhenCredentialsAreNotProvided() {
        runner("", "").run(null);
        runner("   ", PASSWORD).run(null);

        verifyNoInteractions(userRepository, passwordHasher);
    }

    @Test
    void createsAdminWithTrimmedEmailAndHashedPassword() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Mono.empty());
        when(passwordHasher.hash(PASSWORD)).thenReturn(Mono.just("hashed"));
        when(userRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        runner("  " + EMAIL + " ", PASSWORD).run(null);

        var captor = ArgumentCaptor.forClass(Admin.class);
        verify(userRepository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(saved.getPasswordHash()).isEqualTo("hashed");
    }

    @Test
    void skipsWhenAnAdminAlreadyExists() {
        var existing = Admin.builder()
                .id(UUID.randomUUID())
                .fullName("Bootstrap Admin")
                .email(EMAIL)
                .phone("+570000000001")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("000000000")
                .dniType(DniType.CITIZENSHIP_CARD)
                .passwordHash("hashed")
                .build();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Mono.just(existing));

        runner(EMAIL, PASSWORD).run(null);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void skipsWhenTheAccountExistsWithAnotherRole() {
        var tourist = Tourist.builder()
                .id(UUID.randomUUID())
                .fullName("Not An Admin")
                .email(EMAIL)
                .phone("+573000000001")
                .role(UserRole.TOURIST)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .dniNumber("111111111")
                .dniType(DniType.CITIZENSHIP_CARD)
                .passwordHash("hashed")
                .build();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Mono.just(tourist));

        runner(EMAIL, PASSWORD).run(null);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordHasher);
    }
}
