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
    provider_id      UUID,
    guide_id         UUID,
    code             VARCHAR(20),
    CONSTRAINT reservations_number_of_people_positive CHECK (number_of_people > 0),
    CONSTRAINT reservations_total_non_negative CHECK (total >= 0),
    CONSTRAINT reservations_booked_for_after_booked_at CHECK (booked_for > booked_at),
    CONSTRAINT reservations_payment_method_valid CHECK (payment_method IN ('CARD', 'CASH', 'TRANSFER')),
    CONSTRAINT reservations_status_valid CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLATION_REQUESTED', 'REJECTED', 'CANCELLED', 'NO_SHOW', 'COMPLETED'))
);

CREATE INDEX IF NOT EXISTS reservations_tourist_idx ON reservations (tourist_id);
CREATE INDEX IF NOT EXISTS reservations_status_idx ON reservations (status);
CREATE INDEX IF NOT EXISTS reservations_provider_idx ON reservations (provider_id);
CREATE INDEX IF NOT EXISTS reservations_guide_idx ON reservations (guide_id);

-- Upgrade path for databases created before the provider/guide/code columns existed.
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS provider_id UUID;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS guide_id UUID;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS code VARCHAR(20);
CREATE SEQUENCE IF NOT EXISTS reservation_code_seq;
-- Backfill for reservations created before the readable code column existed.
UPDATE reservations SET code = 'R-' || nextval('reservation_code_seq') WHERE code IS NULL;

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
-- Schema for the Users bounded context (PostgreSQL).
-- Joined-table inheritance: a common "users" table plus one table per role.

CREATE TABLE IF NOT EXISTS users (
    id            UUID         PRIMARY KEY,
    full_name     VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    phone         VARCHAR(20)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    status        VARCHAR(30)  NOT NULL,
    signed_up_at  TIMESTAMPTZ  NOT NULL
);

CREATE TABLE IF NOT EXISTS admins (
    id               UUID        PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    dni_number       VARCHAR(30) NOT NULL UNIQUE,
    dni_type         VARCHAR(20) NOT NULL,
    last_access_date TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS guides (
    id                  UUID        PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    dni_number          VARCHAR(30) NOT NULL UNIQUE,
    dni_type            VARCHAR(20) NOT NULL,
    license             VARCHAR(50) NOT NULL UNIQUE,
    start_operation_date DATE
);

CREATE TABLE IF NOT EXISTS destination_managers (
    id                   UUID        PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    nit                  VARCHAR(30) NOT NULL UNIQUE,
    start_operation_date DATE
);

CREATE TABLE IF NOT EXISTS service_providers (
    id                   UUID        PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    nit                  VARCHAR(30) NOT NULL UNIQUE,
    service_type         VARCHAR(50),
    registration_status  VARCHAR(30) NOT NULL,
    latitude             DOUBLE PRECISION,
    longitude            DOUBLE PRECISION,
    start_operation_date DATE
);

CREATE TABLE IF NOT EXISTS tourists (
    id                 UUID        PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    dni_number         VARCHAR(30) NOT NULL UNIQUE,
    dni_type           VARCHAR(20) NOT NULL,
    preferred_language VARCHAR(50),
    birth_date         DATE,
    nationality        VARCHAR(80)
);
-- Schema for the events & activities module (PostgreSQL).
--
-- Mirrors the physical relational model (docs branch: docs/models/DB/sql/MODELO RELACIONAL FISICO.txt)
-- with four intentional adjustments:
--   * ids and fks are stored as uuid (the prototype keeps the UUID identity used across the code base),
--   * evento.expected_attendance is added because the attendance estimation use case persists it,
--   * actividad.guia_id is nullable because an activity may not have a guide, and
--   * start/end columns use timestamptz, the natural mapping for the domain's Instant values.
-- The script is idempotent and runs at startup (spring.sql.init.mode=always).

CREATE TABLE IF NOT EXISTS evento (
    id                  uuid PRIMARY KEY,
    sitio_id            uuid NOT NULL,
    name                varchar(150) NOT NULL,
    category            varchar(50),
    status              varchar(30),
    price               numeric(12, 2) NOT NULL DEFAULT 0,
    max_capacity        integer,
    starts_at           timestamptz NOT NULL,
    ends_at             timestamptz NOT NULL,
    description         text,
    expected_attendance integer NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS actividad (
    id            uuid PRIMARY KEY,
    sitio_id      uuid NOT NULL,
    guia_id       uuid,
    name          varchar(150) NOT NULL,
    type          varchar(50),
    status        varchar(30),
    price         numeric(12, 2) NOT NULL DEFAULT 0,
    max_capacity  integer,
    duration_mins integer,
    starts_at     timestamptz NOT NULL,
    ends_at       timestamptz NOT NULL,
    description   text
);

CREATE INDEX IF NOT EXISTS evento_sitio_idx ON evento (sitio_id);
CREATE INDEX IF NOT EXISTS evento_name_idx ON evento (name);
CREATE INDEX IF NOT EXISTS actividad_sitio_idx ON actividad (sitio_id);
CREATE INDEX IF NOT EXISTS actividad_category_idx ON actividad (type);
-- Schema for the sites & zones module (zones in PostgreSQL; sites live in MongoDB).
-- The zone is the relational parent referenced by sites and by the catalog filters.

CREATE TABLE IF NOT EXISTS zona (
    id   uuid PRIMARY KEY,
    name varchar(150) NOT NULL
);

CREATE INDEX IF NOT EXISTS zona_name_idx ON zona (name);
