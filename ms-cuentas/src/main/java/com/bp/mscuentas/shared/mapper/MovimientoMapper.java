package com.bp.mscuentas.shared.mapper;

import com.bp.mscuentas.domain.model.Movimiento;
import com.bp.mscuentas.infrastructure.adapter.out.persistence.MovimientoEntity;
import com.bp.mscuentas.shared.dto.MovimientoRequestDTO;
import com.bp.mscuentas.shared.dto.MovimientoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "tipoMovimiento", ignore = true)
    @Mapping(target = "saldo", ignore = true)
    Movimiento toDomain(MovimientoRequestDTO dto);

    MovimientoResponseDTO toResponseDTO(Movimiento movimiento);

    MovimientoEntity toEntity(Movimiento movimiento);

    Movimiento toDomain(MovimientoEntity entity);
}