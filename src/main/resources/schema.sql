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
