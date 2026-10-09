package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;

public interface AssignUserRoleUseCasePort {

    User handle(AssignUserRoleCommand command);
}
