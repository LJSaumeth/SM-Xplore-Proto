package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.domain.model.admin.Admin;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Creates the first administrator account at startup when {@code ADMIN_EMAIL}
 * and {@code ADMIN_PASSWORD} are provided and no account exists yet for that
 * email. Idempotent: it is a no-op when the account already exists.
 */
@Slf4j
@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminDniNumber;
    private final DniType adminDniType;

    public AdminBootstrapRunner(UserRepositoryPort userRepository,
                                PasswordHasherPort passwordHasher,
                                @Value("${app.security.bootstrap-admin.email:}") String adminEmail,
                                @Value("${app.security.bootstrap-admin.password:}") String adminPassword,
                                @Value("${app.security.bootstrap-admin.dni-number:000000000}") String adminDniNumber,
                                @Value("${app.security.bootstrap-admin.dni-type:CITIZENSHIP_CARD}") DniType adminDniType) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminDniNumber = adminDniNumber;
        this.adminDniType = adminDniType;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.info("Admin bootstrap skipped: ADMIN_EMAIL/ADMIN_PASSWORD not provided.");
            return;
        }
        var existing = userRepository.findByEmail(adminEmail).block();
        if (existing != null) {
            log.info("Admin bootstrap: account '{}' already exists, skipping.", adminEmail);
            return;
        }
        var admin = Admin.builder()
                .id(UUID.randomUUID())
                .fullName("Bootstrap Admin")
                .email(adminEmail)
                .phone(bootstrapPhone(adminEmail))
                .dniNumber(adminDniNumber)
                .dniType(adminDniType)
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .passwordHash(passwordHasher.hash(adminPassword).block())
                .build();
        admin.validate();
        userRepository.save(admin).block();
        log.info("Admin bootstrap: created administrator account '{}'.", adminEmail);
    }

    private static String bootstrapPhone(String email) {
        var digits = String.format("%09d", Math.abs(email.hashCode()) % 1_000_000_000);
        return "+57" + digits;
    }
}
