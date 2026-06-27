package com.bp.msclientes.shared.exception;

public class ClienteNotFoundException extends BusinessException {

    public ClienteNotFoundException(String mensaje) {
        super("CLIENTE_NOT_FOUND", mensaje);
    }
}
