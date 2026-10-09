package com.smxplore.proto.domain.ports.usecases;

import java.util.UUID;

public interface DeleteOwnAccountUseCasePort {

    void handle(UUID userId);
}
