package com.smxplore.proto.domain.model;

import com.smxplore.proto.domain.exceptions.ReservaItemInvalidoException;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.*;

@Getter
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@Table(name = "reserva_item")

public class ReservaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "evento_id")
    private Integer eventoId;

    @Column(name = "actividad_id")
    private Integer actividadId;

    @Column(name = "servicio_id")
    private Integer servicioId;


    protected ReservaItem() {
    }

    private ReservaItem(Integer eventoId, Integer actividadId, Integer servicioId) {
        int referencias = (eventoId != null ? 1 : 0)
                + (actividadId != null ? 1 : 0)
                + (servicioId != null ? 1 : 0);
        if (referencias != 1) {
            throw new ReservaItemInvalidoException(
                    "Un ítem de reserva debe referenciar exactamente un evento, actividad o servicio");
        }
        this.eventoId = eventoId;
        this.actividadId = actividadId;
        this.servicioId = servicioId;
    }

    public static ReservaItem deEvento(Integer eventoId) {

        return new ReservaItem(eventoId, null, null);
    }


    public static ReservaItem deActividad(Integer actividadId) {
        return new ReservaItem(null, actividadId, null);
    }


    public static ReservaItem deServicio(Integer servicioId) {
        return new ReservaItem(null, null, servicioId);
    }


    void asignarReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public ItemStatus tipo() {
        if (eventoId != null) return ItemStatus.EVENTO;
        if (actividadId != null) return ItemStatus.ACTIVIDAD;
        return ItemStatus.SERVICIO;
    }

}