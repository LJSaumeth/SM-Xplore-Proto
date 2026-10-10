package com.smxplore.proto.domain.exceptions;

public class ReservaNoEncontradaException extends DomainException {

    public ReservaNoEncontradaException(Long reservaId) {
        super("RESERVA_NO_ENCONTRADA", "No existe la reserva con id " + reservaId);
    }
}