package com.bp.msclientes.domain.port.in;

import com.bp.msclientes.domain.model.Cliente;

public interface ActualizarClienteUseCase {
    Cliente actualizar(Long id, Cliente cliente);
    Cliente actualizarParcial(Long id, Cliente cliente);
}
