package com.smxplore.proto.infrastructure.adapters.out.security;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.security.TokenIssuerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

@Component
public class JwtTokenIssuerAdapter implements TokenIssuerPort {

    private static final String ISSUER = "ahorasi";

    private final JwtEncoder jwtEncoder;
    private final Duration expiration;

    public JwtTokenIssuerAdapter(JwtEncoder jwtEncoder,
                                 @Value("${app.security.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    @Override
    public Mono<IssuedToken> issue(User user) {
        return Mono.fromCallable(() -> {
                    var now = Instant.now();
                    var claims = JwtClaimsSet.builder()
                            .issuer(ISSUER)
                            .issuedAt(now)
                            .expiresAt(now.plus(expiration))
                            .subject(user.getId().toString())
                            .claim("email", user.getEmail())
                            .claim("name", user.getFullName())
                            .claim("role", user.getRole().name())
                            .build();
                    var value = jwtEncoder.encode(JwtEncoderParameters.from(
                            JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
                    return new IssuedToken(value, expiration.toSeconds());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
