package com.smxplore.proto.domain.exceptions;

public class CapacidadInsuficienteException extends DomainException {

    public CapacidadInsuficienteException(String recurso, Integer recursoId, int solicitados, int disponibles) {
        super("CAPACIDAD_INSUFICIENTE",
                "%s %d solo tiene %d cupos disponibles y se solicitaron %d"
                        .formatted(recurso, recursoId, disponibles, solicitados));
    }
}
