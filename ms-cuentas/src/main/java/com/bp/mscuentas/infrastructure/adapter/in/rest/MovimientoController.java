package com.bp.mscuentas.infrastructure.adapter.in.rest;

import com.bp.mscuentas.domain.model.Movimiento;
import com.bp.mscuentas.domain.port.in.ObtenerMovimientoUseCase;
import com.bp.mscuentas.domain.port.in.RegistrarMovimientoUseCase;
import com.bp.mscuentas.shared.dto.MovimientoRequestDTO;
import com.bp.mscuentas.shared.dto.MovimientoResponseDTO;
import com.bp.mscuentas.shared.mapper.MovimientoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;
    private final ObtenerMovimientoUseCase obtenerMovimientoUseCase;
    private final MovimientoMapper mapper;

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> registrar(
            @Valid @RequestBody MovimientoRequestDTO request) {
        Movimiento movimiento = mapper.toDomain(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(
                        registrarMovimientoUseCase.registrar(movimiento)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponseDTO(
                obtenerMovimientoUseCase.obtenerMovimientoPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorCuentaId(
            @RequestParam Long cuentaId) {
        return ResponseEntity.ok(obtenerMovimientoUseCase
                .obtenerPorCuentaId(cuentaId)
                .stream().map(mapper::toResponseDTO).toList());
    }
}