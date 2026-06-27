package com.bp.msclientes.shared.mapper;

import com.bp.msclientes.domain.model.Cliente;
import com.bp.msclientes.infrastructure.adapter.out.persistence.ClienteEntity;
import com.bp.msclientes.shared.dto.ClienteRequestDTO;
import com.bp.msclientes.shared.dto.ClienteResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    Cliente toDomain(ClienteRequestDTO dto);

    ClienteResponseDTO toResponseDTO(Cliente cliente);

    @Mapping(target = "id", source = "id")
    ClienteEntity toEntity(Cliente cliente);

    Cliente toDomain(ClienteEntity entity);
}
