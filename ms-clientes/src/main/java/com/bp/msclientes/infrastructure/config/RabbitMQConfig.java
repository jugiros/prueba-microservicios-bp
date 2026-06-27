package com.bp.msclientes.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.clientes}")
    private String exchange;

    @Value("${rabbitmq.queue.cliente-creado}")
    private String queueClienteCreado;

    @Value("${rabbitmq.routing-key.cliente-creado}")
    private String routingKeyClienteCreado;

    @Bean
    public DirectExchange clientesExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue clienteCreadoQueue() {
        return QueueBuilder.durable(queueClienteCreado)
                .withArgument("x-dead-letter-exchange", exchange + ".dlx")
                .build();
    }

    @Bean
    public Binding clienteCreadoBinding() {
        return BindingBuilder
                .bind(clienteCreadoQueue())
                .to(clientesExchange())
                .with(routingKeyClienteCreado);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
