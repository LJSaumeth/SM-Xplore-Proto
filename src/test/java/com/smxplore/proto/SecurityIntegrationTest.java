package com.smxplore.proto;

import com.smxplore.proto.domain.model.destinationmanager.DestinationManager;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.AuthResponse;
import com.smxplore.proto.infrastructure.adapters.in.web.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "app.security.cors.allowed-origins=http://localhost:*",
        "app.security.bootstrap-admin.email=admin@smoke.test",
        "app.security.bootstrap-admin.password=Admin123*"
})
@AutoConfigureWebTestClient
class SecurityIntegrationTest {

    private static final String PASSWORD = "Secret123*";
    private static final String ADMIN_EMAIL = "admin@smoke.test";
    private static final String ADMIN_PASSWORD = "Admin123*";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PasswordHasherPort passwordHasher;

    private record Session(UUID id, String token) {
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8) + "@smoke.test";
    }

    private String uniquePhone() {
        return "+573" + String.format("%08d", Math.abs(UUID.randomUUID().getMostSignificantBits() % 100_000_000));
    }

    private String uniqueDni() {
        return String.format("%08d", Math.abs(UUID.randomUUID().getLeastSignificantBits() % 100_000_000));
    }

    private Session registerAndLogin(String role, String extraFields, String email) {
        var registerBody = """
                {
                  "fullName": "Security Test %s",
                  "email": "%s",
                  "phone": "%s",
                  "password": "%s",
                  "role": "%s"%s
                }
                """.formatted(role, email, uniquePhone(), PASSWORD, role, extraFields);

        var created = webTestClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        return new Session(Objects.requireNonNull(created).id(), login(email, PASSWORD));
    }

    private String login(String email, String password) {
        var auth = webTestClient.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();
        return Objects.requireNonNull(auth).token();
    }

    private Session tourist() {
        return registerAndLogin("TOURIST",
                ",\n                  \"dniNumber\": \"" + uniqueDni()
                        + "\",\n                  \"dniType\": \"CITIZENSHIP_CARD\"",
                uniqueEmail("tourist"));
    }

    private String destinationManagerToken() {
        // Privileged roles cannot self-register, so seed the manager directly
        var email = uniqueEmail("manager");
        var manager = DestinationManager.builder()
                .id(UUID.randomUUID())
                .fullName("Seeded Manager")
                .email(email)
                .phone(uniquePhone())
                .role(UserRole.DESTINATION_MANAGER)
                .status(UserStatus.ACTIVE)
                .signedUpAt(Instant.now())
                .nit("NIT-" + uniqueDni())
                .passwordHash(Objects.requireNonNull(passwordHasher.hash(PASSWORD).block()))
                .build();
        manager.validate();
        userRepository.save(manager).block();
        return login(email, PASSWORD);
    }

    private String eventBody() {
        return """
                {
                  "siteId": "%s",
                  "name": "Security Smoke Event",
                  "category": "Nautical",
                  "price": 1000.00,
                  "maxCapacity": 5,
                  "startsAt": "2026-12-01T10:00:00Z",
                  "endsAt": "2026-12-01T12:00:00Z",
                  "description": "Created by the security integration test."
                }
                """.formatted(UUID.randomUUID());
    }

    @Test
    void protectedEndpointsRequireAValidToken() {
        webTestClient.get().uri("/api/v1/events")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post().uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventBody())
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get().uri("/api/v1/events")
                .headers(h -> h.setBearerAuth("not-a-real-token"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void publicEndpointsRemainOpen() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri("/swagger-ui.html")
                .exchange()
                .expectStatus().is3xxRedirection();

        webTestClient.post().uri("/api/v1/auth/recover-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"email": "nobody@smoke.test"}
                        """)
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void privilegedSelfRegistrationIsRejected() {
        for (var role : new String[]{"ADMIN", "DESTINATION_MANAGER", "GUIDE"}) {
            webTestClient.post().uri("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {"fullName": "Bad %s", "email": "%s", "phone": "%s", "password": "%s",
                             "role": "%s", "dniNumber": "%s", "dniType": "CITIZENSHIP_CARD", "nit": "NIT-1"}
                            """.formatted(role, uniqueEmail(role.toLowerCase()), uniquePhone(), PASSWORD,
                            role, uniqueDni()))
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Test
    void roleRulesAndHeadMethodAreEnforced() {
        var tourist = tourist();
        var manager = destinationManagerToken();
        var admin = login(ADMIN_EMAIL, ADMIN_PASSWORD);

        // any authenticated role can read
        webTestClient.get().uri("/api/v1/events")
                .headers(h -> h.setBearerAuth(tourist.token()))
                .exchange()
                .expectStatus().isOk();

        // a tourist cannot manage events
        webTestClient.post().uri("/api/v1/events")
                .headers(h -> h.setBearerAuth(tourist.token()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventBody())
                .exchange()
                .expectStatus().isForbidden();

        // a destination manager can
        webTestClient.post().uri("/api/v1/events")
                .headers(h -> h.setBearerAuth(manager))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventBody())
                .exchange()
                .expectStatus().isCreated();

        // admin-only surface, including HEAD (regression for the HEAD bypass)
        webTestClient.get().uri("/api/v1/users")
                .headers(h -> h.setBearerAuth(tourist.token()))
                .exchange()
                .expectStatus().isForbidden();

        webTestClient.head().uri("/api/v1/users")
                .headers(h -> h.setBearerAuth(tourist.token()))
                .exchange()
                .expectStatus().isForbidden();

        webTestClient.get().uri("/api/v1/users")
                .headers(h -> h.setBearerAuth(admin))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void profileEndpointsAreSelfOrAdminOnly() {
        var alice = tourist();
        var bob = tourist();
        var admin = login(ADMIN_EMAIL, ADMIN_PASSWORD);

        // alice updates her own profile
        webTestClient.put().uri("/api/v1/users/{userId}", alice.id())
                .headers(h -> h.setBearerAuth(alice.token()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"fullName": "Alice Updated", "email": "%s", "phone": "%s"}
                        """.formatted(uniqueEmail("alice-updated"), uniquePhone()))
                .exchange()
                .expectStatus().isOk();

        // alice cannot touch bob's profile
        webTestClient.put().uri("/api/v1/users/{userId}", bob.id())
                .headers(h -> h.setBearerAuth(alice.token()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"fullName": "Hacked", "email": "%s", "phone": "%s"}
                        """.formatted(uniqueEmail("hacked"), uniquePhone()))
                .exchange()
                .expectStatus().isForbidden();

        // alice cannot delete bob's account
        webTestClient.delete().uri("/api/v1/users/{userId}", bob.id())
                .headers(h -> h.setBearerAuth(alice.token()))
                .exchange()
                .expectStatus().isForbidden();

        // bob deletes his own account
        webTestClient.delete().uri("/api/v1/users/{userId}", bob.id())
                .headers(h -> h.setBearerAuth(bob.token()))
                .exchange()
                .expectStatus().isNoContent();

        // admins may read any profile
        webTestClient.get().uri("/api/v1/users/{userId}", alice.id())
                .headers(h -> h.setBearerAuth(admin))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void corsPreflightIsAllowedForTheFrontend() {
        // Runs against the real HTTP server: the context-bound WebTestClient sends
        // relative request URIs, which Spring 7's CorsUtils cannot parse (it asserts
        // an absolute URI), so preflight would fail for reasons unrelated to config.
        webTestClient.options().uri("/api/v1/events")
                .headers(h -> {
                    h.setOrigin("http://localhost:5173");
                    h.setAccessControlRequestMethod(org.springframework.http.HttpMethod.POST);
                    h.setAccessControlRequestHeaders(java.util.List.of("Authorization", "Content-Type"));
                })
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:5173");
    }
}
