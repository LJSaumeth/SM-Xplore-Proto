package com.smxplore.proto.domain.ports.usecases;

public interface RecoverPasswordUseCasePort {

    void handle(String email);
}
