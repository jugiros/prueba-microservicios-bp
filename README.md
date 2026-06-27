# Prueba Técnica — Arquitectura Microservicios BP

Sistema bancario implementado con arquitectura de microservicios en Java 21 y Spring Boot 4.1.0.

**Autor:** Juan Rosendo Molina León
**Fecha:** Junio 2026

---

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Levantamiento del sistema](#levantamiento-del-sistema)
- [Verificación del sistema](#verificación-del-sistema)
- [Pruebas](#pruebas)
- [Endpoints y orden de ejecución](#endpoints-y-orden-de-ejecución)
- [Validación F1 — F7](#validación-f1--f7)
- [Verbos HTTP — Aclaración técnica](#verbos-http--aclaración-técnica)
- [Endpoint de reportes](#endpoint-de-reportes)
- [Solución de problemas](#solución-de-problemas)
- [Detener el sistema](#detener-el-sistema)

---

## Tecnologías

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 (LTS) | Lenguaje principal |
| Spring Boot | 4.1.0 | Framework de aplicación |
| Spring Data JPA | Incluido en SB 4.1.0 | Acceso a base de datos |
| PostgreSQL | 16-alpine | Base de datos relacional |
| RabbitMQ | 3.13-management-alpine | Comunicación asíncrona entre microservicios |
| MapStruct | 1.6.3 | Conversión entre capas en tiempo de compilación |
| Flyway | 12.4.0 | Migraciones de base de datos |
| SpringDoc OpenAPI | 2.8.6 | Documentación interactiva Swagger |
| Testcontainers | 1.20.4 | Pruebas de integración con BD real |
| Docker + Compose | Latest | Despliegue y orquestación |
| Maven | 3.9.6 | Gestión de dependencias y build |
| Lombok | 1.18.46 | Reducción de boilerplate |

---

## Arquitectura

```
prueba-microservicios-bp/
├── ms-clientes/                  → Puerto 8081 — CRUD de Clientes y Personas
├── ms-cuentas/                   → Puerto 8082 — Cuentas, Movimientos y Reportes
├── docker-compose.yml            → Orquestación de 4 servicios
├── BaseDatos.sql                 → Script SQL con esquema completo
├── banco-api.postman_collection.json
├── ms-clientes/README.md         → Documentación interna de ms-clientes
└── ms-cuentas/README.md          → Documentación interna de ms-cuentas
```

### Patrón — Arquitectura Hexagonal + DDD

Cada microservicio sigue la misma estructura interna:

```
domain/model/                           → Entidades de dominio puras (sin @Entity)
domain/port/in/                         → Interfaces de casos de uso (entrada)
domain/port/out/                        → Interfaces de repositorio (salida)
domain/service/                         → Lógica de negocio
infrastructure/adapter/in/rest/         → Controladores REST
infrastructure/adapter/out/persistence/ → Adaptadores JPA
infrastructure/adapter/out/messaging/   → Adaptadores RabbitMQ
infrastructure/config/                  → Configuración de infraestructura
shared/dto/                             → DTOs de entrada y salida
shared/mapper/                          → Mappers MapStruct
shared/exception/                       → Manejo centralizado de excepciones
```

### Comunicación entre microservicios

```
ms-clientes  ──── RabbitMQ (asíncrono) ────▶  ms-cuentas
                  ClienteCreadoEvent

ms-cuentas   ──── HTTP RestClient (síncrono) ──▶  ms-clientes
                  Solo para reportes F4
```

---

## Requisitos Previos

- **Docker Desktop** instalado y corriendo
- **Postman** para validar los endpoints
- Puertos libres: `5432`, `5672`, `8081`, `8082`, `15672`

> No se requiere Java, Maven ni PostgreSQL instalados localmente — todo corre dentro de Docker.

### Verificar que Docker está corriendo

```powershell
docker ps
```

Resultado esperado — tabla vacía sin errores:
```
CONTAINER ID   IMAGE   COMMAND   CREATED   STATUS   PORTS   NAMES
```

### Verificar puertos libres

```powershell
netstat -an | findstr :5432
netstat -an | findstr :5672
netstat -an | findstr :8081
netstat -an | findstr :8082
netstat -an | findstr :15672
```

Si algún comando retorna resultados el puerto está ocupado. Liberar antes de continuar.

| Puerto | Servicio |
|---|---|
| 5432 | PostgreSQL |
| 5672 | RabbitMQ AMQP |
| 8081 | ms-clientes |
| 8082 | ms-cuentas |
| 15672 | RabbitMQ Management UI |

---

## Levantamiento del Sistema

> Todos los comandos se ejecutan desde la **raíz del repositorio**.

### Paso 1 — Clonar el repositorio

```powershell
git clone <url-del-repositorio>
cd prueba-microservicios-bp
```

### Paso 2 — Limpiar estado previo (si aplica)

```powershell
docker rm -f bp-postgres bp-rabbitmq bp-ms-clientes bp-ms-cuentas
docker-compose down -v
```

### Paso 3 — Levantar el sistema completo

```powershell
docker-compose up --build -d
```

> Primera vez: 5-8 minutos — descarga imágenes y compila el código.
> Siguientes veces: 15-30 segundos.

### Paso 4 — Verificar que todos los contenedores están healthy

```powershell
docker-compose ps
```

Resultado esperado:
```
NAME              STATUS
bp-postgres       Up (healthy)
bp-rabbitmq       Up (healthy)
bp-ms-clientes    Up (healthy)
bp-ms-cuentas     Up (healthy)
```

---

## Verificación del Sistema

### Swagger UI — Documentación interactiva

| Microservicio | URL |
|---|---|
| ms-clientes | http://localhost:8081/swagger-ui.html |
| ms-cuentas | http://localhost:8082/swagger-ui.html |

### Health checks

```powershell
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

### Verificar tablas en PostgreSQL

```powershell
docker exec -it bp-postgres psql -U bpuser -d bpdb -c "\dt"
```

Resultado esperado:
```
 Schema |    Name     | Type  | Owner
--------+-------------+-------+--------
 public | clientes    | table | bpuser
 public | cuentas     | table | bpuser
 public | movimientos | table | bpuser
```

---

## Pruebas

### F5 — Pruebas unitarias

```powershell
cd ms-clientes
mvn test
cd ..
```

Resultado esperado:
```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

Pruebas implementadas en `ClienteServiceTest`:

| Prueba | Qué verifica |
|---|---|
| `debeCrearClienteExitosamente` | Creación exitosa de cliente |
| `debeLanzarExcepcionSiClienteIdDuplicado` | Unicidad de clienteId |
| `debeLanzarExcepcionSiClienteNoExiste` | Error 404 cuando no existe |
| `MsClientesApplicationTests` | Contexto Spring Boot válido |

### F6 — Pruebas de integración

```powershell
cd ms-clientes
mvn test -P integration-test
cd ..
```

Usa Testcontainers para levantar PostgreSQL real en Docker y prueba el flujo completo HTTP → Controller → Service → Repository → BD.

> **Nota Windows:** En Windows con Docker Desktop + WSL2 puede requerir configuración adicional del socket Docker. Ver sección [Solución de problemas](#solución-de-problemas).

---

## Endpoints y Orden de Ejecución

Ejecutar en este orden estrictamente — las cuentas dependen de los clientes creados previamente.

### 1. Crear clientes — `POST http://localhost:8081/api/clientes`

**Jose Lema:**
```json
{
  "nombre": "Jose Lema", "genero": "Masculino", "edad": 30,
  "identificacion": "1234567890", "direccion": "Otavalo sn y principal",
  "telefono": "098254785", "clienteId": "CLI001", "contrasena": "1234", "estado": true
}
```

**Marianela Montalvo:**
```json
{
  "nombre": "Marianela Montalvo", "genero": "Femenino", "edad": 28,
  "identificacion": "0987654321", "direccion": "Amazonas y NNUU",
  "telefono": "097548965", "clienteId": "CLI002", "contrasena": "5678", "estado": true
}
```

**Juan Osorio:**
```json
{
  "nombre": "Juan Osorio", "genero": "Masculino", "edad": 35,
  "identificacion": "1122334455", "direccion": "13 de junio y Equinoccial",
  "telefono": "098874587", "clienteId": "CLI003", "contrasena": "1245", "estado": true
}
```

### 2. Crear cuentas — `POST http://localhost:8082/api/cuentas`

```json
{ "numeroCuenta": "478758", "tipoCuenta": "Ahorros",   "saldoInicial": 2000.00, "clienteId": 1, "estado": true }
{ "numeroCuenta": "225487", "tipoCuenta": "Corriente", "saldoInicial": 100.00,  "clienteId": 2, "estado": true }
{ "numeroCuenta": "495878", "tipoCuenta": "Ahorros",   "saldoInicial": 0.00,    "clienteId": 3, "estado": true }
{ "numeroCuenta": "496825", "tipoCuenta": "Ahorros",   "saldoInicial": 540.00,  "clienteId": 2, "estado": true }
{ "numeroCuenta": "585545", "tipoCuenta": "Corriente", "saldoInicial": 1000.00, "clienteId": 1, "estado": true }
```

### 3. Registrar movimientos — `POST http://localhost:8082/api/movimientos`

| Body | Resultado |
|---|---|
| `{ "valor": -575.00, "cuentaId": 1 }` | Retiro — saldo: 1425.00 |
| `{ "valor":  600.00, "cuentaId": 2 }` | Depósito — saldo: 700.00 |
| `{ "valor":  150.00, "cuentaId": 3 }` | Depósito — saldo: 150.00 |
| `{ "valor": -540.00, "cuentaId": 4 }` | Retiro — saldo: 0.00 |

### 4. Validar F3 — Saldo insuficiente

```json
POST http://localhost:8082/api/movimientos
{ "valor": -1000.00, "cuentaId": 3 }
```

Respuesta esperada:
```json
HTTP 400 Bad Request
{ "mensaje": "Saldo no disponible" }
```

### 5. Reporte F4

```
GET http://localhost:8082/api/reportes?fecha=2026-01-01T00:00:00,2026-12-31T23:59:59&cliente=1
GET http://localhost:8082/api/reportes?fecha=2026-01-01T00:00:00,2026-12-31T23:59:59&cliente=2
GET http://localhost:8082/api/reportes?fecha=2026-01-01T00:00:00,2026-12-31T23:59:59&cliente=3
```

> Usar **año 2026** — los movimientos se registran con la fecha actual del servidor.

---

## Validación F1 — F7

| F | Descripción | Cómo probar |
|---|---|---|
| **F1** | CRUD /clientes — CRU /cuentas /movimientos | Postman — requests de clientes y cuentas |
| **F2** | Movimientos actualizan saldo disponible | Postman — requests de movimientos |
| **F3** | Saldo insuficiente → 400 "Saldo no disponible" | Postman — retiro de 1000 en cuenta con saldo 150 |
| **F4** | Reporte por fecha y cliente → JSON | Postman — GET /api/reportes con parámetros |
| **F5** | Pruebas unitarias Cliente | `mvn test` en ms-clientes |
| **F6** | Prueba de integración | `mvn test -P integration-test` con Docker activo |
| **F7** | Despliegue en Docker | `docker-compose up --build -d` |

---

## Verbos HTTP — Aclaración técnica

El enunciado menciona los verbos: `GET`, `POST`, `PUT`, **`Push`**, `DELETE`.

**`PUSH` no existe como verbo HTTP** según el estándar RFC 9110. Los verbos HTTP oficiales son: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS`, `TRACE`.

Lo que existe con ese nombre es **HTTP/2 Server Push** — un mecanismo donde el servidor envía recursos al cliente sin que los pida, completamente diferente a una operación CRUD.

**Decisión adoptada:** se implementó `PATCH` que es el verbo semánticamente correcto para actualizaciones parciales de un recurso:

| Verbo | Semántica |
|---|---|
| `PUT` | Reemplaza el recurso completo — todos los campos requeridos |
| `PATCH` | Actualiza parcialmente — solo los campos enviados cambian |

---

## Endpoint de Reportes

El enunciado indica el endpoint: `/reportes?fecha=rango fechas`

Se implementó con el parámetro `fecha` recibiendo el rango completo separado por coma y el parámetro `cliente` para el ID del cliente:

```
GET /api/reportes?fecha=2026-01-01T00:00:00,2026-12-31T23:59:59&cliente=1
```

El formato `fecha=inicio,fin` es la convención más adoptada en APIs REST para rangos de fechas — evita ambigüedad y es compatible con todos los clientes HTTP sin necesidad de encoding especial.

---

## Solución de Problemas

### Conflicto de contenedores existentes

```powershell
docker rm -f bp-postgres bp-rabbitmq bp-ms-clientes bp-ms-cuentas
docker-compose up --build -d
```

### Puertos ocupados

```powershell
# Identificar qué proceso usa el puerto (ejemplo puerto 8081)
netstat -an | findstr :8081
```

### Docker Desktop no responde

```powershell
# Verificar que Docker está corriendo
docker ps

# Si falla, iniciar Docker Desktop
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"
Start-Sleep -Seconds 60
docker ps
```

### Contenedor unhealthy — ver logs

```powershell
docker logs bp-ms-clientes --tail=30
docker logs bp-ms-cuentas --tail=30
docker logs bp-postgres --tail=10
docker logs bp-rabbitmq --tail=10
```

### Reconstruir imágenes

```powershell
docker-compose down
docker-compose build --no-cache ms-clientes ms-cuentas
docker-compose up -d
```

### F6 — Pruebas de integración fallan en Windows con WSL2

**Error:** `Could not find a valid Docker environment — NpipeSocketClientProviderStrategy: Status 500`

**Causa:** Testcontainers intenta conectarse al socket de Docker via Named Pipe de Windows, pero Docker Desktop con WSL2 restringe el acceso desde la JVM de Windows.

**Soluciones:**

1. Habilitar TCP en Docker Desktop — `Settings → General → Expose daemon on tcp://localhost:2375` y luego:
```powershell
$env:DOCKER_HOST="tcp://localhost:2375"
mvn test -P integration-test
```

2. En Linux o Mac funciona directamente sin configuración adicional.

> El código de F6 está correctamente implementado. El error es de configuración del entorno Windows, no del código.

---

## Detener el Sistema

```powershell
# Preservar datos de PostgreSQL
docker-compose down

# Reset completo — elimina todos los datos
docker-compose down -v
```

---

## Entregables

| Archivo | Descripción |
|---|---|
| `ms-clientes/` | Microservicio CRUD de Clientes y Personas |
| `ms-cuentas/` | Microservicio de Cuentas, Movimientos y Reportes |
| `docker-compose.yml` | Orquestación completa de 4 servicios |
| `BaseDatos.sql` | Script SQL con esquema completo |
| `banco-api.postman_collection.json` | Colección Postman con todos los endpoints |
| `ms-clientes/README.md` | Documentación interna de ms-clientes |
| `ms-cuentas/README.md` | Documentación interna de ms-cuentas |
