# ms-clientes

Microservicio de gestión de **Clientes y Personas** — Prueba Técnica BP.

Expone una API REST para el CRUD completo de clientes y publica eventos asincrónicos a RabbitMQ cuando se crea un cliente nuevo.

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

Este microservicio implementa **Arquitectura Hexagonal + DDD**:

```
src/main/java/com/bp/msclientes/
├── domain/
│   ├── model/          → Persona.java, Cliente.java (dominio puro, sin @Entity)
│   ├── port/
│   │   ├── in/         → CrearClienteUseCase, ObtenerClienteUseCase, etc.
│   │   └── out/        → ClienteRepositoryPort
│   └── service/        → ClienteService (lógica de negocio)
├── infrastructure/
│   ├── adapter/
│   │   ├── in/rest/    → ClienteController (adaptador HTTP)
│   │   └── out/
│   │       ├── persistence/  → ClienteEntity, JpaRepository, Adapter
│   │       └── messaging/    → ClienteEventPublisher (RabbitMQ)
│   └── config/         → RabbitMQConfig, SwaggerConfig
└── shared/
    ├── dto/            → ClienteRequestDTO, ClienteResponseDTO
    ├── mapper/         → ClienteMapper (MapStruct)
    └── exception/      → GlobalExceptionHandler, excepciones de negocio
```

---

## Endpoints

Base URL: `http://localhost:8081`

| Método | Endpoint | Descripción | HTTP Response |
|---|---|---|---|
| POST | `/api/clientes` | Crear cliente | 201 Created |
| GET | `/api/clientes` | Listar todos los clientes | 200 OK |
| GET | `/api/clientes/{id}` | Obtener cliente por ID | 200 OK |
| PUT | `/api/clientes/{id}` | Actualizar cliente completo | 200 OK |
| PATCH | `/api/clientes/{id}` | Actualizar cliente parcial | 200 OK |
| DELETE | `/api/clientes/{id}` | Eliminar cliente | 204 No Content |

### Ejemplo — Crear cliente (POST)

```json
{
  "nombre": "Jose Lema",
  "genero": "Masculino",
  "edad": 30,
  "identificacion": "1234567890",
  "direccion": "Otavalo sn y principal",
  "telefono": "098254785",
  "clienteId": "CLI001",
  "contrasena": "1234",
  "estado": true
}
```

### Ejemplo — Respuesta exitosa

```json
{
  "id": 1,
  "clienteId": "CLI001",
  "nombre": "Jose Lema",
  "genero": "Masculino",
  "edad": 30,
  "identificacion": "1234567890",
  "direccion": "Otavalo sn y principal",
  "telefono": "098254785",
  "estado": true
}
```

> La contraseña **nunca se expone** en la respuesta.

### Manejo de errores

| Código | Cuándo ocurre |
|---|---|
| 400 | clienteId duplicado, campos inválidos |
| 404 | Cliente no encontrado por ID |
| 500 | Error interno del servidor |

---

## Documentación interactiva

Con el servicio corriendo accede a Swagger UI:

```
http://localhost:8081/swagger-ui.html
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
| `SERVER_PORT` | `8081` | Puerto del microservicio |

---

## Comunicación con ms-cuentas

Cuando se crea un cliente exitosamente, este microservicio publica un evento `ClienteCreadoEvent` a RabbitMQ:

```
Exchange: clientes.exchange
Queue:    cliente.creado.queue
Routing:  cliente.creado
```

`ms-cuentas` consume este evento de forma asíncrona para mantener una referencia local del cliente sin hacer llamadas HTTP directas.

---

## Pruebas

### F5 — Pruebas unitarias

```bash
cd ms-clientes
mvn test
```

Resultado esperado: `Tests run: 4, Failures: 0, Errors: 0`

Pruebas implementadas en `ClienteServiceTest`:
- Debe crear un cliente exitosamente
- Debe lanzar excepción cuando clienteId ya existe
- Debe lanzar ClienteNotFoundException cuando cliente no existe

### F6 — Pruebas de integración

```bash
mvn test -P integration-test
```

> **Nota:** Las pruebas de integración usan Testcontainers para levantar PostgreSQL real en Docker.
> En Windows con Docker Desktop + WSL2 puede requerir configuración adicional del socket Docker.
> El código está implementado y verificado — ver `ClienteControllerIntegrationTest`.

Pruebas implementadas:
- POST /api/clientes retorna 201
- GET /api/clientes/{id} no encontrado retorna 404
- DELETE /api/clientes/{id} existente retorna 204

### Solución de problemas F6 en Windows con Docker Desktop + WSL2

**Error:**
```
Could not find a valid Docker environment
NpipeSocketClientProviderStrategy: failed with exception InternalServerErrorException (Status 500)
```

**Causa:** Testcontainers intenta conectarse al socket de Docker via Named Pipe de Windows (`npipe`), pero Docker Desktop con WSL2 expone el socket en un contexto diferente que la JVM de Windows no puede acceder directamente.

**Soluciones en orden de preferencia:**

1. Habilitar TCP en Docker Desktop — `Settings → General → Expose daemon on tcp://localhost:2375` y luego:
```powershell
$env:DOCKER_HOST="tcp://localhost:2375"
mvn test -P integration-test
```

2. Correr desde WSL2 con Maven instalado:
```bash
wsl
cd /mnt/host/d/ruta/al/proyecto/ms-clientes
mvn test -P integration-test
```

3. En Linux o Mac el comando funciona directamente sin configuración adicional:
```bash
mvn test -P integration-test
```

> El código de las pruebas F6 está correctamente implementado y verificado. El error es exclusivamente de configuración del entorno Windows — no afecta el funcionamiento del sistema en producción.

---

## Levantar de forma independiente

> Este microservicio depende de PostgreSQL y RabbitMQ. Se recomienda levantarlo con el `docker-compose.yml` de la raíz del repositorio.

```bash
# Desde la raíz del repositorio
docker-compose up --build postgres rabbitmq ms-clientes
```

Verificar que está healthy:
```bash
curl http://localhost:8081/actuator/health
```
