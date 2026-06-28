package com.bp.mscuentas.domain.service;

import com.bp.mscuentas.domain.model.Cuenta;
import com.bp.mscuentas.domain.model.Movimiento;
import com.bp.mscuentas.domain.port.out.CuentaRepositoryPort;
import com.bp.mscuentas.domain.port.out.MovimientoRepositoryPort;
import com.bp.mscuentas.infrastructure.adapter.out.rest.ClienteRestClient;
import com.bp.mscuentas.shared.dto.ReporteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;
    private final ClienteRestClient clienteRestClient;

    public List<ReporteDTO> generarReporte(Long clienteId,
                                           LocalDateTime fechaInicio,
                                           LocalDateTime fechaFin) {
        log.info("Generando reporte para clienteId: {} entre {} y {}",
                clienteId, fechaInicio, fechaFin);

        ClienteRestClient.ClienteDTO cliente =
                clienteRestClient.obtenerClientePorId(clienteId);

        List<Cuenta> cuentas = cuentaRepository.buscarPorClienteId(clienteId);
        List<ReporteDTO> reporte = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            List<Movimiento> movimientos =
                    movimientoRepository.buscarPorCuentaIdYFechas(
                            cuenta.getId(), fechaInicio, fechaFin);

            for (Movimiento movimiento : movimientos) {
                reporte.add(ReporteDTO.builder()
                        .cliente(cliente.getNombre())
                        .numeroCuenta(cuenta.getNumeroCuenta())
                        .tipo(cuenta.getTipoCuenta())
                        .saldoInicial(cuenta.getSaldoInicial())
                        .estado(cuenta.getEstado())
                        .fecha(movimiento.getFecha())
                        .movimiento(movimiento.getValor())
                        .saldoDisponible(movimiento.getSaldo())
                        .build());
            }
        }

        log.info("Reporte generado con {} registros", reporte.size());
        return reporte;
    }
}
