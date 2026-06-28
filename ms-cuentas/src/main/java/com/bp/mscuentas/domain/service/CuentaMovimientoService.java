package com.bp.mscuentas.domain.service;

import com.bp.mscuentas.domain.model.Cuenta;
import com.bp.mscuentas.domain.model.Movimiento;
import com.bp.mscuentas.domain.port.in.ActualizarCuentaUseCase;
import com.bp.mscuentas.domain.port.in.CrearCuentaUseCase;
import com.bp.mscuentas.domain.port.in.ObtenerCuentaUseCase;
import com.bp.mscuentas.domain.port.in.ObtenerMovimientoUseCase;
import com.bp.mscuentas.domain.port.in.RegistrarMovimientoUseCase;
import com.bp.mscuentas.domain.port.out.CuentaRepositoryPort;
import com.bp.mscuentas.domain.port.out.MovimientoRepositoryPort;
import com.bp.mscuentas.shared.exception.BusinessException;
import com.bp.mscuentas.shared.exception.CuentaNotFoundException;
import com.bp.mscuentas.shared.exception.MovimientoNotFoundException;
import com.bp.mscuentas.shared.exception.SaldoInsuficienteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaMovimientoService implements
        CrearCuentaUseCase,
        ObtenerCuentaUseCase,
        ActualizarCuentaUseCase,
        RegistrarMovimientoUseCase,
        ObtenerMovimientoUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    @Override
    @Transactional
    public Cuenta crear(Cuenta cuenta) {
        log.info("Creando cuenta numero: {}", cuenta.getNumeroCuenta());
        if (cuentaRepository.existePorNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new BusinessException("Ya existe una cuenta con el numero: "
                    + cuenta.getNumeroCuenta());
        }
        cuenta.setSaldoDisponible(cuenta.getSaldoInicial());
        return cuentaRepository.guardar(cuenta);
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        return cuentaRepository.buscarPorId(id)
                .orElseThrow(() -> new CuentaNotFoundException(
                        "Cuenta no encontrada con id: " + id));
    }

    @Override
    public List<Cuenta> obtenerTodas() {
        return cuentaRepository.buscarTodas();
    }

    @Override
    public List<Cuenta> obtenerPorClienteId(Long clienteId) {
        return cuentaRepository.buscarPorClienteId(clienteId);
    }

    @Override
    @Transactional
    public Cuenta actualizar(Long id, Cuenta cuenta) {
        Cuenta existente = obtenerPorId(id);
        existente.setNumeroCuenta(cuenta.getNumeroCuenta());
        existente.setTipoCuenta(cuenta.getTipoCuenta());
        existente.setEstado(cuenta.getEstado());
        return cuentaRepository.actualizar(existente);
    }

    @Override
    @Transactional
    public Cuenta actualizarParcial(Long id, Cuenta cuenta) {
        Cuenta existente = obtenerPorId(id);
        if (cuenta.getNumeroCuenta() != null) existente.setNumeroCuenta(cuenta.getNumeroCuenta());
        if (cuenta.getTipoCuenta() != null) existente.setTipoCuenta(cuenta.getTipoCuenta());
        if (cuenta.getEstado() != null) existente.setEstado(cuenta.getEstado());
        return cuentaRepository.actualizar(existente);
    }

    @Override
    @Transactional
    public Movimiento registrar(Movimiento movimiento) {
        log.info("Registrando movimiento en cuenta id: {}", movimiento.getCuentaId());
        Cuenta cuenta = cuentaRepository.buscarPorId(movimiento.getCuentaId())
                .orElseThrow(() -> new CuentaNotFoundException(
                        "Cuenta no encontrada con id: " + movimiento.getCuentaId()));

        double nuevoSaldo = cuenta.getSaldoDisponible() + movimiento.getValor();

        if (nuevoSaldo < 0) {
            throw new SaldoInsuficienteException("Saldo no disponible");
        }

        movimiento.setFecha(LocalDateTime.now());
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setTipoMovimiento(movimiento.getValor() >= 0 ? "Deposito" : "Retiro");

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.actualizar(cuenta);

        Movimiento guardado = movimientoRepository.guardar(movimiento);
        log.info("Movimiento registrado. Nuevo saldo: {}", nuevoSaldo);
        return guardado;
    }

    @Override
    public Movimiento obtenerMovimientoPorId(Long id) {
        return movimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new MovimientoNotFoundException(
                        "Movimiento no encontrado con id: " + id));
    }

    @Override
    public List<Movimiento> obtenerPorCuentaId(Long cuentaId) {
        return movimientoRepository.buscarPorCuentaId(cuentaId);
    }
}
