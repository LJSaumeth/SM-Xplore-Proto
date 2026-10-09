package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.ports.repository.UserSearchCriteria;

import java.util.List;

public interface SearchUserUseCasePort {

    List<User> handle(UserSearchCriteria criteria);
}
