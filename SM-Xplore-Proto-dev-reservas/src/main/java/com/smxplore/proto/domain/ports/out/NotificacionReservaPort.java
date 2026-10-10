package com.smxplore.proto.domain.ports.out;

import com.smxplore.proto.domain.model.Reserva;

public interface NotificacionReservaPort {

    void notificarNuevaReserva(Reserva reserva);

    void notificarCambioEstado(Reserva reserva);
}