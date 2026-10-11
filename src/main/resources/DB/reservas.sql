CREATE TABLE reserva (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    turista_id       UUID          NOT NULL REFERENCES turista (id),
    status           VARCHAR(30)   NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLATION_REQUESTED',
                          'REJECTED', 'CANCELLED', 'NO_SHOW', 'COMPLETED')),
    total            NUMERIC(12,2) NOT NULL CHECK (total >= 0),
    number_of_people INTEGER       NOT NULL CHECK (number_of_people > 0),
    booked_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    booked_for       TIMESTAMPTZ   NOT NULL,
    notes            TEXT,
    payment_method   VARCHAR(50)
);

CREATE INDEX idx_reserva_turista ON reserva (turista_id);
CREATE INDEX idx_reserva_status  ON reserva (status);

CREATE TABLE reserva_item (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reserva_id   UUID NOT NULL REFERENCES reserva (id) ON DELETE CASCADE,
    evento_id    UUID REFERENCES evento (id),
    actividad_id UUID REFERENCES actividad (id),
    servicio_id  UUID REFERENCES servicio (id),
    -- exactamente una de las tres referencias
    CONSTRAINT ck_reserva_item_un_destino
        CHECK (num_nonnulls(evento_id, actividad_id, servicio_id) = 1)
);

CREATE INDEX idx_reserva_item_reserva ON reserva_item (reserva_id);
