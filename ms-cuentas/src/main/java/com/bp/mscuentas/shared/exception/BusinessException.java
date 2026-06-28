package com.bp.mscuentas.shared.exception;

public class BusinessException extends RuntimeException {
    private final String codigo;

    public BusinessException(String mensaje) {
        super(mensaje);
        this.codigo = "BUSINESS_ERROR";
    }

    public BusinessException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
}