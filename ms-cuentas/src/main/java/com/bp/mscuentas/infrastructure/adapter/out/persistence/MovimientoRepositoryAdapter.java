package com.bp.mscuentas.infrastructure.adapter.out.persistence;

import com.bp.mscuentas.domain.model.Movimiento;
import com.bp.mscuentas.domain.port.out.MovimientoRepositoryPort;
import com.bp.mscuentas.shared.mapper.MovimientoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;
    private final MovimientoMapper mapper;

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(movimiento)));
    }

    @Override
    public Optional<Movimiento> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Movimiento> buscarPorCuentaId(Long cuentaId) {
        return jpaRepository.findByCuentaId(cuentaId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Movimiento> buscarPorCuentaIdYFechas(Long cuentaId,
                                                     LocalDateTime fechaInicio,
                                                     LocalDateTime fechaFin) {
        return jpaRepository.findByCuentaIdAndFechaBetween(cuentaId, fechaInicio, fechaFin)
                .stream().map(mapper::toDomain).toList();
    }
}