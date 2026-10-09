package com.smxplore.proto.domain.ports.usecases;

import com.smxplore.proto.domain.model.user.User;

public interface BlockOrSuspendUserUseCasePort {

    User handle(BlockOrSuspendUserCommand command);
}
