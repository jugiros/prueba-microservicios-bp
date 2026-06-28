# ms-cuentas

Microservicio de gestión de **Cuentas, Movimientos y Reportes** — Prueba Técnica BP.

Expone una API REST para CRU de cuentas, registro de movimientos con actualización de saldo, validación de saldo insuficiente (F3) y generación de reportes por fecha y cliente (F4). Consume eventos asincrónicos de RabbitMQ publicados por `ms-clientes`.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| PostgreSQL | 16-alpine |
| RabbitMQ | 3.13 |
| MapStruct | 1.6.3 |
| Flyway | 12.4.0 |
| SpringDoc OpenAPI | 2.8.6 |

---

## Arquitectura

```
src/main/java/com/bp/mscuentas/
├── domain/
│   ├── model/          → Cuenta.java, Movimiento.java (dominio puro, sin @Entity)
│   ├── port/
│   │   ├── in/         → CrearCuentaUseCase, ObtenerCuentaUseCase, ActualizarCuentaUseCase
│   │   │               → RegistrarMovimientoUseCase, ObtenerMovimientoUseCase
│   │   └── out/        → CuentaRepositoryPort, MovimientoRepositoryPort
│   └── service/        → CuentaMovimientoService, ReporteService
├── infrastructure/
│   ├── adapter/
│   │   ├── in/rest/    → CuentaController, MovimientoController, ReporteController
│   │   └── out/
│   │       ├── persistence/  → Entities, JpaRepositories, Adapters
│   │       ├── messaging/    → ClienteEventConsumer (RabbitMQ)
│   │       └── rest/         → ClienteRestClient (HTTP a ms-clientes)
│   └── config/         → RabbitMQConfig, SwaggerConfig
└── shared/
    ├── dto/            → CuentaRequestDTO, CuentaResponseDTO, MovimientoRequestDTO,
    │                     MovimientoResponseDTO, ReporteDTO
    ├── mapper/         → CuentaMapper, MovimientoMapper
    └── exception/      → GlobalExceptionHandler, SaldoInsuficienteException, etc.
```

---

## Endpoints

Base URL: `http://localhost:8082`

### Cuentas

| Método | Endpoint | Descripción | HTTP Response |
|---|---|---|---|
| POST | `/api/cuentas` | Crear cuenta | 201 Created |
| GET | `/api/cuentas` | Listar todas las cuentas | 200 OK |
| GET | `/api/cuentas/{id}` | Obtener cuenta por ID | 200 OK |
| PUT | `/api/cuentas/{id}` | Actualizar cuenta completa | 200 OK |
| PATCH | `/api/cuentas/{id}` | Actualizar cuenta parcial | 200 OK |

### Movimientos

| Método | Endpoint | Descripción | HTTP Response |
|---|---|---|---|
| POST | `/api/movimientos` | Registrar movimiento | 201 Created |
| GET | `/api/movimientos/{id}` | Obtener movimiento por ID | 200 OK |
| GET | `/api/movimientos?cuentaId={id}` | Listar por cuenta | 200 OK |

### Reportes — F4

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/reportes?fecha=inicio,fin&cliente=id` | Reporte de estado de cuenta |

Ejemplo:
```
GET /api/reportes?fecha=2026-01-01T00:00:00,2026-12-31T23:59:59&cliente=1
```

### Ejemplos de request

**Crear cuenta:**
```json
{
  "numeroCuenta": "478758",
  "tipoCuenta": "Ahorros",
  "saldoInicial": 2000.00,
  "clienteId": 1,
  "estado": true
}
```

**Registrar movimiento (retiro):**
```json
{ "valor": -575.00, "cuentaId": 1 }
```

**Registrar movimiento (depósito):**
```json
{ "valor": 600.00, "cuentaId": 2 }
```

### F3 — Saldo insuficiente

Cuando el retiro supera el saldo disponible el sistema responde:
```json
HTTP 400 Bad Request
{ "mensaje": "Saldo no disponible" }
```

---

## Documentación interactiva

```
http://localhost:8082/swagger-ui.html
```

---

## Variables de entorno

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL |
| `DB_PORT` | `5432` | Puerto de PostgreSQL |
| `DB_NAME` | `bpdb` | Nombre de la base de datos |
| `DB_USER` | `bpuser` | Usuario de PostgreSQL |
| `DB_PASSWORD` | `bppassword` | Contraseña de PostgreSQL |
| `RABBITMQ_HOST` | `localhost` | Host de RabbitMQ |
| `RABBITMQ_PORT` | `5672` | Puerto AMQP de RabbitMQ |
| `RABBITMQ_USER` | `bpuser` | Usuario de RabbitMQ |
| `RABBITMQ_PASSWORD` | `bppassword` | Contraseña de RabbitMQ |
| `MS_CLIENTES_URL` | `http://localhost:8081` | URL de ms-clientes para reportes F4 |
| `SERVER_PORT` | `8082` | Puerto del microservicio |

---

## Comunicación con ms-clientes

### Consumo asíncrono — RabbitMQ

Cuando `ms-clientes` crea un cliente publica un evento `ClienteCreadoEvent`:

```
Exchange: clientes.exchange
Queue:    cliente.creado.queue
Routing:  cliente.creado
```

`ms-cuentas` consume este evento via `ClienteEventConsumer` y lo registra en el log.

### Consulta síncrona — HTTP RestClient

Para generar el reporte F4, `ms-cuentas` consulta `ms-clientes` via HTTP para obtener el nombre del cliente:

```
GET http://ms-clientes:8081/api/clientes/{id}
```

---

## Migraciones Flyway

| Script | Tabla | Descripción |
|---|---|---|
| `V1__crear_tabla_cuentas.sql` | `cuentas` | Número de cuenta, tipo, saldo inicial, saldo disponible, estado, clienteId |
| `V2__crear_tabla_movimientos.sql` | `movimientos` | Fecha, tipo, valor, saldo resultante, cuentaId con FK hacia cuentas |

---

## Pruebas

### F5 — Pruebas unitarias

```bash
cd ms-cuentas
mvn test
```

Pruebas implementadas en `CuentaMovimientoServiceTest`:

| Prueba | Qué verifica |
|---|---|
| `debeCrearCuentaExitosamente` | Creación exitosa con saldo disponible = saldo inicial |
| `debeLanzarExcepcionSiNumeroCuentaDuplicado` | Unicidad del número de cuenta |
| `debeLanzarSaldoInsuficienteException` | **F3** — mensaje exacto "Saldo no disponible" |

### F6 — Pruebas de integración

```bash
mvn test -P integration-test
```

Pruebas implementadas en `CuentaControllerIntegrationTest`:

| Prueba | Qué verifica |
|---|---|
| `crearCuenta_retorna201` | POST crea cuenta en PostgreSQL real — saldo disponible = saldo inicial |
| `obtenerCuenta_noExiste_retorna404` | GlobalExceptionHandler retorna 404 |
| `crearCuenta_numeroDuplicado_retorna400` | Constraint de unicidad en BD real |

> **Nota Windows:** Las pruebas de integración usan Testcontainers que requiere acceso al socket Docker. Ver sección de troubleshooting.

### Solución de problemas F6 en Windows con Docker Desktop + WSL2

**Error:**
```
Could not find a valid Docker environment
NpipeSocketClientProviderStrategy: failed with exception InternalServerErrorException (Status 500)
```

**Causa:** Testcontainers intenta conectarse via Named Pipe de Windows pero Docker Desktop con WSL2 restringe el acceso desde la JVM.

**Soluciones:**

1. Habilitar TCP en Docker Desktop — `Settings → General → Expose daemon on tcp://localhost:2375`:
```powershell
$env:DOCKER_HOST="tcp://localhost:2375"
mvn test -P integration-test
```

2. En Linux o Mac funciona directamente sin configuración adicional.

---

## Levantar de forma independiente

```bash
# Desde la raíz del repositorio
docker-compose up --build postgres rabbitmq ms-cuentas

# Verificar health
curl http://localhost:8082/actuator/health
```

> `ms-cuentas` depende de PostgreSQL, RabbitMQ y `ms-clientes` para el reporte F4. Se recomienda levantar todo el sistema con `docker-compose up --build -d`.

---

## Solución de problemas RabbitMQ — Queue con argumentos distintos

**Error:**
```
PRECONDITION_FAILED - inequivalent arg 'x-dead-letter-exchange' for queue 'cliente.creado.queue'
```

**Causa:** La queue existe en RabbitMQ con argumentos de una ejecución anterior incompatibles con la configuración actual.

**Solución:**
```bash
docker-compose down -v
docker-compose up --build -d
```

El `-v` elimina los volúmenes y RabbitMQ recrea la queue desde cero.
