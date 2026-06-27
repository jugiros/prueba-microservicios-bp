package com.bp.msclientes.infrastructure.adapter.in.rest;

import com.bp.msclientes.domain.model.Cliente;
import com.bp.msclientes.domain.port.in.ActualizarClienteUseCase;
import com.bp.msclientes.domain.port.in.CrearClienteUseCase;
import com.bp.msclientes.domain.port.in.EliminarClienteUseCase;
import com.bp.msclientes.domain.port.in.ObtenerClienteUseCase;
import com.bp.msclientes.shared.dto.ClienteRequestDTO;
import com.bp.msclientes.shared.dto.ClienteResponseDTO;
import com.bp.msclientes.shared.mapper.ClienteMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final ObtenerClienteUseCase obtenerClienteUseCase;
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final EliminarClienteUseCase eliminarClienteUseCase;
    private final ClienteMapper mapper;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(
            @Valid @RequestBody ClienteRequestDTO request) {
        log.info("POST /api/clientes");
        Cliente cliente = mapper.toDomain(request);
        Cliente creado = crearClienteUseCase.crear(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(creado));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obtenerTodos() {
        log.info("GET /api/clientes");
        List<ClienteResponseDTO> clientes = obtenerClienteUseCase.obtenerTodos()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/clientes/{}", id);
        Cliente cliente = obtenerClienteUseCase.obtenerPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request) {
        log.info("PUT /api/clientes/{}", id);
        Cliente cliente = mapper.toDomain(request);
        Cliente actualizado = actualizarClienteUseCase.actualizar(id, cliente);
        return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody ClienteRequestDTO request) {
        log.info("PATCH /api/clientes/{}", id);
        Cliente cliente = mapper.toDomain(request);
        Cliente actualizado = actualizarClienteUseCase.actualizarParcial(id, cliente);
        return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{}", id);
        eliminarClienteUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
