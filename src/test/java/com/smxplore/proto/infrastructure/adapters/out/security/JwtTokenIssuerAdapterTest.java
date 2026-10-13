package com.smxplore.proto.infrastructure.adapters.out.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.smxplore.proto.domain.model.tourist.Tourist;
import com.smxplore.proto.domain.model.user.DniType;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenIssuerAdapterTest {

    private static final String SECRET = "test-secret-with-at-least-32-bytes!!";

    private final ReactiveJwtDecoder decoder = decoderFor(SECRET);

    private final JwtTokenIssuerAdapter adapter = new JwtTokenIssuerAdapter(
            new NimbusJwtEncoder(new ImmutableSecret<>(SECRET.getBytes(StandardCharsets.UTF_8))), 60);

    private static ReactiveJwtDecoder decoderFor(String secret) {
        return NimbusReactiveJwtDecoder
                .withSecretKey(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

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
    void issuesSignedTokenWithUserClaims() {
        var user = tourist();

        var issued = adapter.issue(user).block();

        assertThat(issued).isNotNull();
        assertThat(issued.expiresInSeconds()).isEqualTo(3600);

        var jwt = decoder.decode(issued.value()).block();
        assertThat(jwt).isNotNull();
        assertThat(jwt.getSubject()).isEqualTo(user.getId().toString());
        assertThat(jwt.getClaimAsString("email")).isEqualTo("john.doe@example.com");
        assertThat(jwt.getClaimAsString("name")).isEqualTo("John Doe");
        assertThat(jwt.getClaimAsString("role")).isEqualTo("TOURIST");
        assertThat(jwt.getIssuer().toString()).isEqualTo("https://ahorasi.local");
        assertThat(jwt.getIssuedAt()).isNotNull();
        assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt());
    }

    @Test
    void rejectsTokensSignedWithADifferentSecret() {
        var otherDecoder = decoderFor("another-test-secret-32-bytes-minimum!!");
        var issued = adapter.issue(tourist()).block();

        assertThat(issued).isNotNull();
        assertThatThrownBy(() -> otherDecoder.decode(issued.value()).block())
                .isInstanceOf(JwtException.class);
    }
}
