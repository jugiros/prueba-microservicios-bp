package com.bp.mscuentas.infrastructure.adapter.out.persistence;

import com.bp.mscuentas.domain.model.Cuenta;
import com.bp.mscuentas.domain.port.out.CuentaRepositoryPort;
import com.bp.mscuentas.shared.mapper.CuentaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;
    private final CuentaMapper mapper;

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cuenta)));
    }

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(mapper::toDomain);
    }

    @Override
    public List<Cuenta> buscarTodas() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cuenta> buscarPorClienteId(Long clienteId) {
        return jpaRepository.findByClienteId(clienteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Cuenta actualizar(Cuenta cuenta) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cuenta)));
    }

    @Override
    public boolean existePorNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }
}