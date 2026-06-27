package com.bp.msclientes.infrastructure.adapter.in.rest;

import com.bp.msclientes.shared.dto.ClienteRequestDTO;
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
 * F6 - Prueba de Integración ClienteController
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
@DisplayName("F6 - Prueba de Integración ClienteController")
class ClienteControllerIntegrationTest {

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

    private ClienteRequestDTO buildClienteRequest(String identificacion, String clienteId) {
        return ClienteRequestDTO.builder()
                .nombre("Jose Lema")
                .genero("Masculino")
                .edad(30)
                .identificacion(identificacion)
                .direccion("Otavalo sn y principal")
                .telefono("098254785")
                .clienteId(clienteId)
                .contrasena("1234")
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/clientes - Crear cliente exitosamente retorna 201")
    void crearCliente_retorna201() throws Exception {
        ClienteRequestDTO request = buildClienteRequest("1234567890", "CLI001");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.identificacion").value("1234567890"))
                .andExpect(jsonPath("$.estado").value(true));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Cliente no encontrado retorna 404")
    void obtenerCliente_noExiste_retorna404() throws Exception {
        mockMvc.perform(get("/api/clientes/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} - Eliminar cliente existente retorna 204")
    void eliminarCliente_existente_retorna204() throws Exception {
        ClienteRequestDTO request = buildClienteRequest("1122334455", "CLI003");

        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/clientes/" + id))
                .andExpect(status().isNoContent());
    }
}
