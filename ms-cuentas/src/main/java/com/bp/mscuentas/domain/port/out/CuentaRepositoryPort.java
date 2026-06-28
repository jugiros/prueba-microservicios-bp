package com.bp.mscuentas.domain.port.out;

import com.bp.mscuentas.domain.model.Cuenta;
import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {
    Cuenta guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorId(Long id);
    Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta);
    List<Cuenta> buscarTodas();
    List<Cuenta> buscarPorClienteId(Long clienteId);
    Cuenta actualizar(Cuenta cuenta);
    boolean existePorNumeroCuenta(String numeroCuenta);
}