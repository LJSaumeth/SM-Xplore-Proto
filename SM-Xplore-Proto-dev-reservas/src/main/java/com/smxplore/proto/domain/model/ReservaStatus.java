package com.smxplore.proto.domain.model;

import java.util.Set;


public enum ReservaStatus {
    PENDIENTE,
    CONFIRMADA,
    CANCELACION_SOLICITADA,
    RECHAZADA,
    CANCELADA,
    NO_SHOW,
    COMPLETADA;

    public Set<ReservaStatus> transicionesPermitidas() {
        return switch (this) {
            case PENDIENTE -> Set.of(CONFIRMADA, RECHAZADA, CANCELADA);
            case CONFIRMADA -> Set.of(CANCELACION_SOLICITADA, CANCELADA, NO_SHOW, COMPLETADA);
            case CANCELACION_SOLICITADA -> Set.of(CANCELADA, CONFIRMADA);
            case RECHAZADA, CANCELADA, NO_SHOW, COMPLETADA -> Set.of();
        };
    }

    public boolean puedeTransitarA(ReservaStatus destino) {
        return transicionesPermitidas().contains(destino);
    }

    public boolean esFinal() {
        return transicionesPermitidas().isEmpty();
    }
}