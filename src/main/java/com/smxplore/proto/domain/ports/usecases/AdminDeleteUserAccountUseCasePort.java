package com.smxplore.proto.domain.ports.usecases;

import java.util.UUID;

public interface AdminDeleteUserAccountUseCasePort {

    void handle(UUID userId);
}
