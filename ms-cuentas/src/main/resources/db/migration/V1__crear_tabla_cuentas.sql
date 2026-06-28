CREATE TABLE IF NOT EXISTS cuentas (
    id                BIGSERIAL       PRIMARY KEY,
    numero_cuenta     VARCHAR(20)     NOT NULL UNIQUE,
    tipo_cuenta       VARCHAR(50)     NOT NULL,
    saldo_inicial     DECIMAL(19,2)   NOT NULL,
    saldo_disponible  DECIMAL(19,2)   NOT NULL,
    estado            BOOLEAN         NOT NULL DEFAULT TRUE,
    cliente_id        BIGINT          NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_cuentas_numero_cuenta ON cuentas(numero_cuenta);
CREATE INDEX IF NOT EXISTS idx_cuentas_cliente_id ON cuentas(cliente_id);
