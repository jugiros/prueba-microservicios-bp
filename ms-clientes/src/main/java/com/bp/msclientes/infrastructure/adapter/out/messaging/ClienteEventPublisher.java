package com.bp.msclientes.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.clientes}")
    private String exchange;

    @Value("${rabbitmq.routing-key.cliente-creado}")
    private String routingKeyClienteCreado;

    public void publicarClienteCreado(Long clienteId, String nombre) {
        ClienteCreadoEvent event = new ClienteCreadoEvent(clienteId, nombre);
        log.info("Publicando evento ClienteCreado para clienteId: {}", clienteId);
        rabbitTemplate.convertAndSend(exchange, routingKeyClienteCreado, event);
        log.info("Evento ClienteCreado publicado exitosamente");
    }

    public record ClienteCreadoEvent(Long clienteId, String nombre) {}
}
