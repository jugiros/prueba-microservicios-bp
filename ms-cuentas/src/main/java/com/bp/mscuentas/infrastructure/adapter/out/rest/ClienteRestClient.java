package com.bp.mscuentas.infrastructure.adapter.out.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ClienteRestClient {

    private final RestClient restClient;

    public ClienteRestClient(@Value("${ms-clientes.url}") String msClientesUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(msClientesUrl)
                .build();
    }

    public ClienteDTO obtenerClientePorId(Long id) {
        return restClient.get()
                .uri("/api/clientes/{id}", id)
                .retrieve()
                .body(ClienteDTO.class);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ClienteDTO {
        private Long id;
        private String clienteId;
        private String nombre;
        private String identificacion;
        private Boolean estado;
    }
}
