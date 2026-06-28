package com.bp.mscuentas.shared.exception;

public class MovimientoNotFoundException extends BusinessException {
    public MovimientoNotFoundException(String mensaje) {
        super("MOVIMIENTO_NOT_FOUND", mensaje);
    }
}