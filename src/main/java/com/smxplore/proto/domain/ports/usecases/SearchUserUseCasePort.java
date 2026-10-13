package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;

import reactor.core.publisher.Flux;

public interface SearchUserUseCasePort {

    Flux<User> handle(UserSearchCriteria criteria);
}
