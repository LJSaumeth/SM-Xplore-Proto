package com.smxplore.proto.domain.ports.out;


public interface DisponibilidadPort {

    int cuposDisponiblesEvento(Integer eventoId);

    int cuposDisponiblesActividad(Integer actividadId);
}