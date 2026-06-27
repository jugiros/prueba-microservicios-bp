package com.bp.msclientes.domain.port.out;

import com.bp.msclientes.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorClienteId(String clienteId);
    List<Cliente> buscarTodos();
    Cliente actualizar(Cliente cliente);
    void eliminar(Long id);
    boolean existePorClienteId(String clienteId);
}
