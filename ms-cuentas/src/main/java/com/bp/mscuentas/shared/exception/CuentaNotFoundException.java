package com.bp.mscuentas.shared.exception;

public class CuentaNotFoundException extends BusinessException {
    public CuentaNotFoundException(String mensaje) {
        super("CUENTA_NOT_FOUND", mensaje);
    }
}