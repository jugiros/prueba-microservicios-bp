package com.bp.msclientes.infrastructure.adapter.out.persistence;

import com.bp.msclientes.domain.model.Cliente;
import com.bp.msclientes.domain.port.out.ClienteRepositoryPort;
import com.bp.msclientes.shared.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;
    private final ClienteMapper mapper;

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = mapper.toEntity(cliente);
        ClienteEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Cliente actualizar(Cliente cliente) {
        ClienteEntity entity = mapper.toEntity(cliente);
        ClienteEntity updated = jpaRepository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorClienteId(String clienteId) {
        return jpaRepository.existsByClienteId(clienteId);
    }
}
