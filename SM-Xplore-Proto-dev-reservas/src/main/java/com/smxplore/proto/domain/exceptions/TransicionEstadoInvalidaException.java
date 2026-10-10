package com.smxplore.proto.domain.exceptions;

import com.smxplore.proto.domain.model.ReservaStatus;

public class TransicionEstadoInvalidaException extends DomainException {

    public TransicionEstadoInvalidaException(ReservaStatus actual, ReservaStatus destino) {
        super("TRANSICION_ESTADO_INVALIDA",
                "No se puede pasar una reserva de " + actual + " a " + destino);
    }
}