package com.bp.msclientes.domain.port.in;

import com.bp.msclientes.domain.model.Cliente;

public interface CrearClienteUseCase {
    Cliente crear(Cliente cliente);
}
