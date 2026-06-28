package com.bp.mscuentas.infrastructure.adapter.in.rest;

import com.bp.mscuentas.domain.model.Cuenta;
import com.bp.mscuentas.domain.port.in.ActualizarCuentaUseCase;
import com.bp.mscuentas.domain.port.in.CrearCuentaUseCase;
import com.bp.mscuentas.domain.port.in.ObtenerCuentaUseCase;
import com.bp.mscuentas.shared.dto.CuentaRequestDTO;
import com.bp.mscuentas.shared.dto.CuentaResponseDTO;
import com.bp.mscuentas.shared.mapper.CuentaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CrearCuentaUseCase crearCuentaUseCase;
    private final ObtenerCuentaUseCase obtenerCuentaUseCase;
    private final ActualizarCuentaUseCase actualizarCuentaUseCase;
    private final CuentaMapper mapper;

    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crear(
            @Valid @RequestBody CuentaRequestDTO request) {
        Cuenta cuenta = mapper.toDomain(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(crearCuentaUseCase.crear(cuenta)));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(obtenerCuentaUseCase.obtenerTodas()
                .stream().map(mapper::toResponseDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponseDTO(
                obtenerCuentaUseCase.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CuentaRequestDTO request) {
        Cuenta cuenta = mapper.toDomain(request);
        return ResponseEntity.ok(mapper.toResponseDTO(
                actualizarCuentaUseCase.actualizar(id, cuenta)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody CuentaRequestDTO request) {
        Cuenta cuenta = mapper.toDomain(request);
        return ResponseEntity.ok(mapper.toResponseDTO(
                actualizarCuentaUseCase.actualizarParcial(id, cuenta)));
    }
}