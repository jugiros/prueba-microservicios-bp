package com.bp.mscuentas.domain.service;

import com.bp.mscuentas.domain.model.Cuenta;
import com.bp.mscuentas.domain.model.Movimiento;
import com.bp.mscuentas.domain.port.out.CuentaRepositoryPort;
import com.bp.mscuentas.domain.port.out.MovimientoRepositoryPort;
import com.bp.mscuentas.shared.exception.BusinessException;
import com.bp.mscuentas.shared.exception.CuentaNotFoundException;
import com.bp.mscuentas.shared.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("F5 - CuentaMovimientoService - Pruebas Unitarias")
class CuentaMovimientoServiceTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @InjectMocks
    private CuentaMovimientoService service;

    private Cuenta cuentaValida;

    @BeforeEach
    void setUp() {
        cuentaValida = Cuenta.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipoCuenta("Ahorros")
                .saldoInicial(2000.00)
                .saldoDisponible(2000.00)
                .estado(true)
                .clienteId(1L)
                .build();
    }

    @Test
    @DisplayName("Debe crear cuenta exitosamente con saldo disponible igual al inicial")
    void debeCrearCuentaExitosamente() {
        when(cuentaRepository.existePorNumeroCuenta(anyString())).thenReturn(false);
        when(cuentaRepository.guardar(any(Cuenta.class))).thenReturn(cuentaValida);

        Cuenta resultado = service.crear(cuentaValida);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getSaldoDisponible()).isEqualTo(2000.00);
        verify(cuentaRepository).guardar(any(Cuenta.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando numero de cuenta ya existe")
    void debeLanzarExcepcionSiNumeroCuentaDuplicado() {
        when(cuentaRepository.existePorNumeroCuenta(anyString())).thenReturn(true);

        assertThatThrownBy(() -> service.crear(cuentaValida))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una cuenta con el numero");
    }

    @Test
    @DisplayName("F3 - Debe lanzar SaldoInsuficienteException con mensaje Saldo no disponible")
    void debeLanzarSaldoInsuficienteException() {
        when(cuentaRepository.buscarPorId(1L)).thenReturn(Optional.of(cuentaValida));

        Movimiento retiroExcesivo = Movimiento.builder()
                .valor(-9999.00)
                .cuentaId(1L)
                .build();

        assertThatThrownBy(() -> service.registrar(retiroExcesivo))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessage("Saldo no disponible");
    }
}
