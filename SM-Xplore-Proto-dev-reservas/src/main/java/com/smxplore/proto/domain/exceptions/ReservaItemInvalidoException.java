package com.smxplore.proto.domain.exceptions;

// Se lanza cuando un ítem de reserva no referencia exactamente un evento, actividad o servicio. */

public class ReservaItemInvalidoException extends DomainException {

    public ReservaItemInvalidoException(String message) {
        super("RESERVA ITEM INVALIDO", message);
    }
}