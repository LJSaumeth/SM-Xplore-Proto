package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.exceptions.InvalidCredentialsException;
import com.smxplore.proto.domain.model.user.LoginResult;
import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.security.PasswordHasherPort;
import com.smxplore.proto.domain.ports.security.TokenIssuerPort;
import com.smxplore.proto.domain.ports.usecases.LoginUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase implements LoginUseCasePort {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenIssuerPort tokenIssuer;

    @Override
    public Mono<LoginResult> handle(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> passwordHasher.matches(rawPassword, user.getPasswordHash())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                        .thenReturn(user))
                .doOnNext(User::ensureActive)
                .flatMap(user -> tokenIssuer.issue(user)
                        .map(token -> new LoginResult(user, token.value(), token.expiresInSeconds())));
    }
}
