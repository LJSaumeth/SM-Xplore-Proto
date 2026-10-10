package com.smxplore.proto.domain.exceptions;


//Se lanza cuando los datos de una reserva no cumplen las reglas de negocio.

public class ReservaInvalidaException extends DomainException {

    public ReservaInvalidaException(String message) {
        super("RESERVA_INVALIDA", message);
    }
}