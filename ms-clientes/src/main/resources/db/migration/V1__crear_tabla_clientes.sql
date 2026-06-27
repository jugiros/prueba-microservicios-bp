CREATE TABLE IF NOT EXISTS clientes (
    id              BIGSERIAL       PRIMARY KEY,
    cliente_id      VARCHAR(20)     NOT NULL UNIQUE,
    nombre          VARCHAR(100)    NOT NULL,
    genero          VARCHAR(20)     NOT NULL,
    edad            INTEGER         NOT NULL,
    identificacion  VARCHAR(20)     NOT NULL UNIQUE,
    direccion       VARCHAR(200)    NOT NULL,
    telefono        VARCHAR(20)     NOT NULL,
    contrasena      VARCHAR(255)    NOT NULL,
    estado          BOOLEAN         NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_clientes_cliente_id ON clientes(cliente_id);
CREATE INDEX IF NOT EXISTS idx_clientes_identificacion ON clientes(identificacion);
