package com.bp.mscuentas.shared.mapper;

import com.bp.mscuentas.domain.model.Cuenta;
import com.bp.mscuentas.infrastructure.adapter.out.persistence.CuentaEntity;
import com.bp.mscuentas.shared.dto.CuentaRequestDTO;
import com.bp.mscuentas.shared.dto.CuentaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saldoDisponible", ignore = true)
    Cuenta toDomain(CuentaRequestDTO dto);

    CuentaResponseDTO toResponseDTO(Cuenta cuenta);

    CuentaEntity toEntity(Cuenta cuenta);

    Cuenta toDomain(CuentaEntity entity);
}