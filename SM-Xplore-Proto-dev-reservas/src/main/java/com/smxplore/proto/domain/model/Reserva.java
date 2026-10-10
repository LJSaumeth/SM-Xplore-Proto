package com.smxplore.proto.domain.model;

import com.smxplore.proto.domain.exceptions.ReservaInvalidaException;
import com.smxplore.proto.domain.exceptions.TransicionEstadoInvalidaException;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;


@Getter
@Entity
@Table(name = "reserva")
@AllArgsConstructor
@Builder(toBuilder = true)
public class Reserva {

    private static final int MAX_PAYMENT_METHOD = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turista_id", nullable = false)
    private Integer turistaId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private ReservaStatus status;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "number_of_people", nullable = false)
    private int numberOfPeople;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    @Column(name = "booked_for", nullable = false)
    private LocalDateTime bookedFor;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "payment_method", length = MAX_PAYMENT_METHOD)
    private String paymentMethod;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaItem> items = new ArrayList<>();

    protected Reserva() {
    }

    public static Reserva crear(Integer turistaId, int numberOfPeople, BigDecimal total,
                                LocalDateTime bookedFor, String notes, String paymentMethod,
                                List<ReservaItem> items, Clock clock) { LocalDateTime ahora = LocalDateTime.now(clock);

        if (turistaId == null) {
            throw new ReservaInvalidaException("La reserva debe tener un turista");
        }
        if (numberOfPeople <= 0) {
            throw new ReservaInvalidaException("El número de personas debe ser mayor a cero");
        }
        if (total == null || total.signum() < 0) {
            throw new ReservaInvalidaException("El total no puede ser nulo ni negativo");
        }
        if (bookedFor == null || !bookedFor.isAfter(ahora)) {
            throw new ReservaInvalidaException("La fecha de la reserva debe ser futura");
        }
        if (items == null || items.isEmpty()) {
            throw new ReservaInvalidaException("La reserva debe tener al menos un ítem");
        }
        if (paymentMethod != null && paymentMethod.length() > MAX_PAYMENT_METHOD) {
            throw new ReservaInvalidaException(
                    "El método de pago no puede superar " + MAX_PAYMENT_METHOD + " caracteres");
        }

        Reserva reserva = new Reserva();
        reserva.turistaId = turistaId;
        reserva.status = ReservaStatus.PENDIENTE;
        reserva.total = total.setScale(2, RoundingMode.HALF_UP);
        reserva.numberOfPeople = numberOfPeople;
        reserva.bookedAt = ahora;
        reserva.bookedFor = bookedFor;
        reserva.notes = notes;
        reserva.paymentMethod = paymentMethod;
        items.forEach(reserva::agregarItem);
        return reserva;
    }

    private void agregarItem(ReservaItem item) {
        item.asignarReserva(this);
        this.items.add(item);
    }

    public void confirmar(){
        cambiarEstado(ReservaStatus.CONFIRMADA);
    }

    public void rechazar(){
        cambiarEstado(ReservaStatus.RECHAZADA);
    }

    public void solicitarCancelacion() {
        cambiarEstado(ReservaStatus.CANCELACION_SOLICITADA);
    }

    public void cancelar() {
        cambiarEstado(ReservaStatus.CANCELADA);
    }

    public void registrarNoShow(){
        cambiarEstado(ReservaStatus.NO_SHOW);
    }

    public void registrarAsistencia(){
        cambiarEstado(ReservaStatus.COMPLETADA);
    }

    public void cambiarEstado(ReservaStatus destino) {
        if (!status.puedeTransitarA(destino)) {
            throw new TransicionEstadoInvalidaException(status, destino);
        }
        this.status = destino;
    }



}