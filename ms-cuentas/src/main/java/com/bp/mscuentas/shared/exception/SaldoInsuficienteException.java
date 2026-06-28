package com.bp.mscuentas.shared.exception;

public class SaldoInsuficienteException extends BusinessException {
    public SaldoInsuficienteException(String mensaje) {
        super("SALDO_NO_DISPONIBLE", mensaje);
    }
}