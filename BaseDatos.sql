-- =============================================================================
-- BaseDatos.sql
-- Prueba Técnica BP — Sistema Bancario Microservicios
-- =============================================================================
-- Este script crea el esquema completo de la base de datos y carga los datos
-- de prueba del enunciado. Flyway ejecuta las migraciones automáticamente al
-- arrancar cada microservicio, por lo que este archivo es un entregable
-- de documentación / ejecución manual opcional.
--
-- Ejecución manual:
--   docker exec -it bp-postgres psql -U bpuser -d bpdb -f /BaseDatos.sql
-- =============================================================================

-- -----------------------------------------------------------------------------
-- LIMPIEZA (orden inverso por FK)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS movimientos CASCADE;
DROP TABLE IF EXISTS cuentas CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- -----------------------------------------------------------------------------
-- TABLA: clientes
-- Creada por ms-clientes via Flyway V1__crear_tabla_clientes.sql
-- -----------------------------------------------------------------------------
CREATE TABLE clientes (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(255)    NOT NULL,
    genero          VARCHAR(50),
    edad            INTEGER,
    identificacion  VARCHAR(50)     NOT NULL UNIQUE,
    direccion       VARCHAR(255),
    telefono        VARCHAR(50),
    cliente_id      VARCHAR(50)     NOT NULL UNIQUE,
    contrasena      VARCHAR(255)    NOT NULL,
    estado          BOOLEAN         NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_clientes_cliente_id ON clientes(cliente_id);
CREATE INDEX idx_clientes_identificacion ON clientes(identificacion);

-- -----------------------------------------------------------------------------
-- TABLA: cuentas
-- Creada por ms-cuentas via Flyway V1__crear_tabla_cuentas.sql
-- -----------------------------------------------------------------------------
CREATE TABLE cuentas (
    id                BIGSERIAL       PRIMARY KEY,
    numero_cuenta     VARCHAR(20)     NOT NULL UNIQUE,
    tipo_cuenta       VARCHAR(50)     NOT NULL,
    saldo_inicial     DECIMAL(19,2)   NOT NULL,
    saldo_disponible  DECIMAL(19,2)   NOT NULL,
    estado            BOOLEAN         NOT NULL DEFAULT TRUE,
    cliente_id        BIGINT          NOT NULL
);

CREATE INDEX idx_cuentas_numero_cuenta ON cuentas(numero_cuenta);
CREATE INDEX idx_cuentas_cliente_id ON cuentas(cliente_id);

-- -----------------------------------------------------------------------------
-- TABLA: movimientos
-- Creada por ms-cuentas via Flyway V2__crear_tabla_movimientos.sql
-- -----------------------------------------------------------------------------
CREATE TABLE movimientos (
    id               BIGSERIAL       PRIMARY KEY,
    fecha            TIMESTAMP       NOT NULL DEFAULT NOW(),
    tipo_movimiento  VARCHAR(20)     NOT NULL,
    valor            DECIMAL(19,2)   NOT NULL,
    saldo            DECIMAL(19,2)   NOT NULL,
    cuenta_id        BIGINT          NOT NULL,
    CONSTRAINT fk_movimiento_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id)
);

CREATE INDEX idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);

-- -----------------------------------------------------------------------------
-- DATOS DE PRUEBA — Clientes (del enunciado del PDF)
-- -----------------------------------------------------------------------------
INSERT INTO clientes (nombre, genero, edad, identificacion, direccion, telefono, cliente_id, contrasena, estado)
VALUES
    ('Jose Lema',          'Masculino', 30, '1234567890', 'Otavalo sn y principal',    '098254785', 'CLI001', '1234', TRUE),
    ('Marianela Montalvo', 'Femenino',  28, '0987654321', 'Amazonas y NNUU',           '097548965', 'CLI002', '5678', TRUE),
    ('Juan Osorio',        'Masculino', 35, '1122334455', '13 de junio y Equinoccial', '098874587', 'CLI003', '1245', TRUE);

-- -----------------------------------------------------------------------------
-- DATOS DE PRUEBA — Cuentas (del enunciado del PDF)
-- Saldo disponible = saldo inicial al momento de la creación
-- -----------------------------------------------------------------------------
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id)
VALUES
    ('478758', 'Ahorros',   2000.00, 2000.00, TRUE, 1),  -- Jose Lema
    ('225487', 'Corriente',  100.00,  100.00, TRUE, 2),  -- Marianela Montalvo
    ('495878', 'Ahorros',      0.00,    0.00, TRUE, 3),  -- Juan Osorio
    ('496825', 'Ahorros',    540.00,  540.00, TRUE, 2),  -- Marianela Montalvo
    ('585545', 'Corriente', 1000.00, 1000.00, TRUE, 1);  -- Jose Lema

-- -----------------------------------------------------------------------------
-- DATOS DE PRUEBA — Movimientos (del enunciado del PDF)
-- Resultado final de saldos tras aplicar los movimientos
-- -----------------------------------------------------------------------------

-- Cuenta 478758 (Jose Lema, Ahorros) — retiro de 575: saldo 2000 -> 1425
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id)
VALUES (NOW(), 'Retiro', -575.00, 1425.00, 1);

UPDATE cuentas SET saldo_disponible = 1425.00 WHERE id = 1;

-- Cuenta 225487 (Marianela, Corriente) — depósito de 600: saldo 100 -> 700
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id)
VALUES (NOW(), 'Deposito', 600.00, 700.00, 2);

UPDATE cuentas SET saldo_disponible = 700.00 WHERE id = 2;

-- Cuenta 495878 (Juan Osorio, Ahorros) — depósito de 150: saldo 0 -> 150
-- Nota: el retiro de -1000 es rechazado por saldo insuficiente (F3), no se registra
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id)
VALUES (NOW(), 'Deposito', 150.00, 150.00, 3);

UPDATE cuentas SET saldo_disponible = 150.00 WHERE id = 3;

-- Cuenta 496825 (Marianela, Ahorros) — retiro de 540: saldo 540 -> 0
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id)
VALUES (NOW(), 'Retiro', -540.00, 0.00, 4);

UPDATE cuentas SET saldo_disponible = 0.00 WHERE id = 4;

-- Cuenta 585545 (Jose Lema, Corriente) — sin movimientos

-- -----------------------------------------------------------------------------
-- VERIFICACIÓN
-- -----------------------------------------------------------------------------
SELECT 'clientes' AS tabla, COUNT(*) AS registros FROM clientes
UNION ALL
SELECT 'cuentas',    COUNT(*) FROM cuentas
UNION ALL
SELECT 'movimientos', COUNT(*) FROM movimientos;
