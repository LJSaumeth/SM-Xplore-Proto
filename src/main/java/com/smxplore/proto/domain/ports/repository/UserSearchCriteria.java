package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.user.User;
import com.smxplore.proto.domain.model.user.UserRole;
import com.smxplore.proto.domain.model.user.UserStatus;

public record UserSearchCriteria(
        UserRole role,
        UserStatus status,
        String term) {
}
