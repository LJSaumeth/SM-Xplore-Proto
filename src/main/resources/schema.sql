-- Esquema PostgreSQL de reservas (Spring Data R2DBC).
--
-- Notas:
--  * Los nombres de las tablas son plurales, por lo que las entidades R2DBC
--    deben anotarse con @Table("reservations") y @Table("reservation_items").
--    Las columnas sí siguen la convención por defecto de Spring Data
--    (camelCase -> snake_case).
--  * Los enums del dominio se guardan como VARCHAR con CHECK.
--  * Requiere PostgreSQL 13+ por gen_random_uuid().
--  * Se ejecuta al arrancar la app cuando spring.sql.init.mode=always
--    (perfil docker); el script es idempotente.

CREATE TABLE IF NOT EXISTS reservations (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tourist_id       UUID                     NOT NULL,
    number_of_people INTEGER                  NOT NULL,
    total            NUMERIC(18, 2)           NOT NULL,
    payment_method   VARCHAR(20)              NOT NULL,
    status           VARCHAR(30)              NOT NULL,
    booked_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    booked_for       TIMESTAMP WITH TIME ZONE NOT NULL,
    notes            TEXT,
    CONSTRAINT reservations_number_of_people_positive CHECK (number_of_people > 0),
    CONSTRAINT reservations_total_non_negative CHECK (total >= 0),
    CONSTRAINT reservations_booked_for_after_booked_at CHECK (booked_for > booked_at),
    CONSTRAINT reservations_payment_method_valid CHECK (payment_method IN ('CARD', 'CASH', 'TRANSFER')),
    CONSTRAINT reservations_status_valid CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLATION_REQUESTED', 'REJECTED', 'CANCELLED', 'NO_SHOW', 'COMPLETED'))
);

CREATE INDEX IF NOT EXISTS reservations_tourist_idx ON reservations (tourist_id);
CREATE INDEX IF NOT EXISTS reservations_status_idx ON reservations (status);

CREATE TABLE IF NOT EXISTS reservation_items (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id UUID           NOT NULL REFERENCES reservations (id) ON DELETE CASCADE,
    item_id        UUID           NOT NULL,
    type           VARCHAR(20)    NOT NULL,
    price          NUMERIC(14, 2) NOT NULL,
    CONSTRAINT reservation_items_price_non_negative CHECK (price >= 0),
    CONSTRAINT reservation_items_type_valid CHECK (type IN ('EVENT', 'ACTIVITY', 'SERVICE'))
);

CREATE INDEX IF NOT EXISTS reservation_items_reservation_idx ON reservation_items (reservation_id);
CREATE INDEX IF NOT EXISTS reservation_items_item_idx ON reservation_items (item_id);
CREATE INDEX IF NOT EXISTS reservation_items_type_idx ON reservation_items (type);
