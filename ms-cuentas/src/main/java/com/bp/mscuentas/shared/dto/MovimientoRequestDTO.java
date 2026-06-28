package com.bp.mscuentas.shared.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MovimientoRequestDTO {

    @NotNull(message = "El valor del movimiento es obligatorio")
    private Double valor;

    @NotNull(message = "El id de la cuenta es obligatorio")
    private Long cuentaId;
}