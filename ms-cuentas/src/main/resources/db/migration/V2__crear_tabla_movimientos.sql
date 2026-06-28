CREATE TABLE IF NOT EXISTS movimientos (
    id               BIGSERIAL       PRIMARY KEY,
    fecha            TIMESTAMP       NOT NULL,
    tipo_movimiento  VARCHAR(20)     NOT NULL,
    valor            DECIMAL(19,2)   NOT NULL,
    saldo            DECIMAL(19,2)   NOT NULL,
    cuenta_id        BIGINT          NOT NULL,
    CONSTRAINT fk_movimiento_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id)
);

CREATE INDEX IF NOT EXISTS idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos(fecha);
