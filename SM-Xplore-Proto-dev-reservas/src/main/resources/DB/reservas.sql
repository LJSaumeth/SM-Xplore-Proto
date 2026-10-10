-- Enum de estados (debe coincidir con ReservaStatus.java)
CREATE TYPE estado_reserva AS ENUM (
    'PENDIENTE', 'CONFIRMADA', 'CANCELACION_SOLICITADA',
    'RECHAZADA', 'CANCELADA', 'NO_SHOW', 'COMPLETADA'
);

CREATE TABLE reserva (
                         id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         turista_id       INTEGER        NOT NULL REFERENCES turista (id),
                         status           estado_reserva NOT NULL DEFAULT 'PENDIENTE',
                         total            NUMERIC(12,2)  NOT NULL CHECK (total >= 0),
                         number_of_people INTEGER        NOT NULL CHECK (number_of_people > 0),
                         booked_at        TIMESTAMP      NOT NULL DEFAULT now(),
                         booked_for       TIMESTAMP      NOT NULL,
                         notes            TEXT,
                         payment_method   VARCHAR(50)
);

CREATE INDEX idx_reserva_turista ON reserva (turista_id);
CREATE INDEX idx_reserva_status  ON reserva (status);

CREATE TABLE reserva_item (
                              id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              reserva_id   BIGINT  NOT NULL REFERENCES reserva (id) ON DELETE CASCADE,
                              evento_id    INTEGER REFERENCES evento (id),
                              actividad_id INTEGER REFERENCES actividad (id),
                              servicio_id  INTEGER REFERENCES servicio (id),
    -- exactamente una de las tres referencias
                              CONSTRAINT ck_reserva_item_un_destino
                                  CHECK (num_nonnulls(evento_id, actividad_id, servicio_id) = 1)
);

CREATE INDEX idx_reserva_item_reserva ON reserva_item (reserva_id);