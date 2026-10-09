package com.smxplore.proto.domain.ports.usecases;

public interface ResetUserPasswordUseCasePort {

    void handle(ResetUserPasswordCommand command);
}
