package com.smxplore.proto.application.users;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserRepositoryPort;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;
import com.smxplore.proto.domain.ports.usecases.SearchUserUseCasePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class SearchUserUseCase implements SearchUserUseCasePort {

    private final UserRepositoryPort userRepository;

    @Override
    public Flux<User> handle(UserSearchCriteria criteria) {
        return userRepository.search(criteria);
    }
}
