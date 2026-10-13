package com.smxplore.proto.domain.exceptions;

public class UserPhoneAlreadyRegisteredException extends RuntimeException {

    public UserPhoneAlreadyRegisteredException(String phone) {
        super("The phone '" + phone + "' is already registered.");
    }
}
