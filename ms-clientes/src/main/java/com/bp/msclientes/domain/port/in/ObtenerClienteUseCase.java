package com.bp.msclientes.domain.port.in;

import com.bp.msclientes.domain.model.Cliente;
import java.util.List;

public interface ObtenerClienteUseCase {
    Cliente obtenerPorId(Long id);
    List<Cliente> obtenerTodos();
}
