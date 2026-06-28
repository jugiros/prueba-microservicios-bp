package com.bp.mscuentas.infrastructure.adapter.in.rest;

import com.bp.mscuentas.domain.service.ReporteService;
import com.bp.mscuentas.shared.dto.ReporteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * F4 — Reporte de Estado de Cuenta
     *
     * Parámetros según el enunciado del PDF:
     *   - fecha: rango de fechas separado por coma en formato ISO 8601
     *            Ejemplo: 2026-01-01T00:00:00,2026-12-31T23:59:59
     *   - cliente: ID del cliente
     *
     * Ejemplo de uso:
     *   GET /api/reportes?fecha=2026-01-01T00:00:00,2026-12-31T23:59:59&cliente=1
     */
    @GetMapping
    public ResponseEntity<List<ReporteDTO>> generarReporte(
            @RequestParam String fecha,
            @RequestParam Long cliente) {

        log.info("GET /api/reportes - cliente: {}, fecha: {}", cliente, fecha);

        String[] partes = fecha.split(",");
        LocalDateTime fechaInicio = LocalDateTime.parse(partes[0].trim());
        LocalDateTime fechaFin = LocalDateTime.parse(partes[1].trim());

        return ResponseEntity.ok(
                reporteService.generarReporte(cliente, fechaInicio, fechaFin));
    }
}
