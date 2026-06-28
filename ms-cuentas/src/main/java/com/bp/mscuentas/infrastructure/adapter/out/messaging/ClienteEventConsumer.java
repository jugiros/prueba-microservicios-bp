package com.bp.mscuentas.infrastructure.adapter.out.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClienteEventConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.cliente-creado}")
    public void consumirClienteCreado(ClienteCreadoEvent event) {
        log.info("Evento ClienteCreado recibido — clienteId: {}, nombre: {}",
                event.clienteId(), event.nombre());
    }

    public record ClienteCreadoEvent(Long clienteId, String nombre) {}
}