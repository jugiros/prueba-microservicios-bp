package com.bp.mscuentas.infrastructure.adapter.in.rest;

import com.bp.mscuentas.shared.dto.CuentaRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * F6 - Prueba de Integración CuentaController
 *
 * Requiere Docker Desktop activo.
 * Ejecutar con: mvn test -P integration-test
 *
 * Usa Testcontainers para levantar PostgreSQL 16-alpine real en Docker
 * y prueba el flujo completo HTTP -> Controller -> Service -> Repository -> BD real.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("F6 - Prueba de Integración CuentaController")
class CuentaControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bpdb_test")
            .withUsername("bpuser")
            .withPassword("bppassword");

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    private CuentaRequestDTO buildCuentaRequest(String numeroCuenta, Long clienteId) {
        return CuentaRequestDTO.builder()
                .numeroCuenta(numeroCuenta)
                .tipoCuenta("Ahorros")
                .saldoInicial(2000.00)
                .clienteId(clienteId)
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/cuentas - Crear cuenta exitosamente retorna 201")
    void crearCuenta_retorna201() throws Exception {
        CuentaRequestDTO request = buildCuentaRequest("478758", 1L);

        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("478758"))
                .andExpect(jsonPath("$.saldoDisponible").value(2000.00));
    }

    @Test
    @DisplayName("GET /api/cuentas/{id} - Cuenta no encontrada retorna 404")
    void obtenerCuenta_noExiste_retorna404() throws Exception {
        mockMvc.perform(get("/api/cuentas/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/cuentas - Numero de cuenta duplicado retorna 400")
    void crearCuenta_numeroDuplicado_retorna400() throws Exception {
        CuentaRequestDTO request = buildCuentaRequest("478758", 1L);

        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
