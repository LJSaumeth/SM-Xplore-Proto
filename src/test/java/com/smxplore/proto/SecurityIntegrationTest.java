package com.smxplore.proto;

import com.smxplore.proto.infrastructure.adapters.in.web.dto.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;
import java.util.UUID;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SecurityIntegrationTest {

    private static final String PASSWORD = "Secret123*";

    @Autowired
    private WebTestClient webTestClient;

    private String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8) + "@smoke.test";
    }

    private String uniquePhone() {
        return "+573" + String.format("%08d", Math.abs(UUID.randomUUID().getMostSignificantBits()) % 100_000_000);
    }

    private String registerAndLogin(String role, String extraFields) {
        var email = uniqueEmail(role.toLowerCase());
        var registerBody = """
                {
                  "fullName": "Security Test %s",
                  "email": "%s",
                  "phone": "%s",
                  "password": "%s",
                  "role": "%s"%s
                }
                """.formatted(role, email, uniquePhone(), PASSWORD, role, extraFields);

        webTestClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerBody)
                .exchange()
                .expectStatus().isCreated();

        var auth = webTestClient.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        return Objects.requireNonNull(auth).token();
    }

    private String touristToken() {
        return registerAndLogin("TOURIST",
                ",\n                  \"dniNumber\": \"" + Math.abs(UUID.randomUUID().getLeastSignificantBits() % 100_000_000)
                        + "\",\n                  \"dniType\": \"CITIZENSHIP_CARD\"");
    }

    private String destinationManagerToken() {
        return registerAndLogin("DESTINATION_MANAGER",
                ",\n                  \"nit\": \"NIT-" + Math.abs(UUID.randomUUID().getLeastSignificantBits() % 100_000_000) + "\"");
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
    void protectedEndpointsRequireAToken() {
        webTestClient.get().uri("/api/v1/events")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post().uri("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventBody())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void roleRulesAreEnforced() {
        var tourist = touristToken();
        var manager = destinationManagerToken();

        // any authenticated role can read
        webTestClient.get().uri("/api/v1/events")
                .headers(h -> h.setBearerAuth(tourist))
                .exchange()
                .expectStatus().isOk();

        // a tourist cannot manage events
        webTestClient.post().uri("/api/v1/events")
                .headers(h -> h.setBearerAuth(tourist))
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

        // user administration is admin-only
        webTestClient.get().uri("/api/v1/users")
                .headers(h -> h.setBearerAuth(tourist))
                .exchange()
                .expectStatus().isForbidden();
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
