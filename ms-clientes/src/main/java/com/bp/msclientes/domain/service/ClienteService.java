package com.bp.msclientes.domain.service;

import com.bp.msclientes.domain.model.Cliente;
import com.bp.msclientes.domain.port.in.ActualizarClienteUseCase;
import com.bp.msclientes.domain.port.in.CrearClienteUseCase;
import com.bp.msclientes.domain.port.in.EliminarClienteUseCase;
import com.bp.msclientes.domain.port.in.ObtenerClienteUseCase;
import com.bp.msclientes.domain.port.out.ClienteRepositoryPort;
import com.bp.msclientes.shared.exception.BusinessException;
import com.bp.msclientes.shared.exception.ClienteNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService implements
        CrearClienteUseCase,
        ObtenerClienteUseCase,
        ActualizarClienteUseCase,
        EliminarClienteUseCase {

    private final ClienteRepositoryPort repositoryPort;

    @Override
    public Cliente crear(Cliente cliente) {
        log.info("Creando cliente con identificacion: {}", cliente.getIdentificacion());

        if (cliente.getClienteId() == null || cliente.getClienteId().isBlank()) {
            cliente.setClienteId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        if (repositoryPort.existePorClienteId(cliente.getClienteId())) {
            throw new BusinessException("Ya existe un cliente con el clienteId: " + cliente.getClienteId());
        }

        Cliente creado = repositoryPort.guardar(cliente);
        log.info("Cliente creado exitosamente con id: {}", creado.getId());
        return creado;
    }

    @Override
    public Cliente obtenerPorId(Long id) {
        log.info("Buscando cliente con id: {}", id);
        return repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con id: " + id));
    }

    @Override
    public List<Cliente> obtenerTodos() {
        log.info("Obteniendo todos los clientes");
        return repositoryPort.buscarTodos();
    }

    @Override
    public Cliente actualizar(Long id, Cliente cliente) {
        log.info("Actualizando cliente con id: {}", id);
        Cliente existente = obtenerPorId(id);
        existente.setNombre(cliente.getNombre());
        existente.setGenero(cliente.getGenero());
        existente.setEdad(cliente.getEdad());
        existente.setIdentificacion(cliente.getIdentificacion());
        existente.setDireccion(cliente.getDireccion());
        existente.setTelefono(cliente.getTelefono());
        existente.setContrasena(cliente.getContrasena());
        existente.setEstado(cliente.getEstado());
        return repositoryPort.actualizar(existente);
    }

    @Override
    public Cliente actualizarParcial(Long id, Cliente cliente) {
        log.info("Actualizando parcialmente cliente con id: {}", id);
        Cliente existente = obtenerPorId(id);
        if (cliente.getNombre() != null) existente.setNombre(cliente.getNombre());
        if (cliente.getGenero() != null) existente.setGenero(cliente.getGenero());
        if (cliente.getEdad() != null) existente.setEdad(cliente.getEdad());
        if (cliente.getIdentificacion() != null) existente.setIdentificacion(cliente.getIdentificacion());
        if (cliente.getDireccion() != null) existente.setDireccion(cliente.getDireccion());
        if (cliente.getTelefono() != null) existente.setTelefono(cliente.getTelefono());
        if (cliente.getContrasena() != null) existente.setContrasena(cliente.getContrasena());
        if (cliente.getEstado() != null) existente.setEstado(cliente.getEstado());
        return repositoryPort.actualizar(existente);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando cliente con id: {}", id);
        obtenerPorId(id);
        repositoryPort.eliminar(id);
        log.info("Cliente eliminado exitosamente con id: {}", id);
    }
}
