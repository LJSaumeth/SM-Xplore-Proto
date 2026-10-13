package com.smxplore.proto.infrastructure.adapters.out.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenIssuerAdapterTest {

    private static final String SECRET = "test-secret-with-at-least-32-bytes!!";

    private final ReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
            .withSecretKey(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
            .macAlgorithm(MacAlgorithm.HS256)
            .build();

    private final JwtTokenIssuerAdapter adapter = new JwtTokenIssuerAdapter(
            new NimbusJwtEncoder(new ImmutableSecret<>(SECRET.getBytes(StandardCharsets.UTF_8))), 60);

    @Test
    void issuesSignedTokenWithUserClaims() {
        var user = Tourist.builder()
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

        var issued = adapter.issue(user).block();

        assertThat(issued).isNotNull();
        assertThat(issued.expiresInSeconds()).isEqualTo(3600);

        var jwt = decoder.decode(issued.value()).block();
        assertThat(jwt).isNotNull();
        assertThat(jwt.getSubject()).isEqualTo(user.getId().toString());
        assertThat(jwt.getClaimAsString("email")).isEqualTo("john.doe@example.com");
        assertThat(jwt.getClaimAsString("name")).isEqualTo("John Doe");
        assertThat(jwt.getClaimAsString("role")).isEqualTo("TOURIST");
    }
}
