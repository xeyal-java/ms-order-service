package com.example.msorderservice.producer;

import com.example.msorderservice.config.RabbitMqConfig;
import com.example.msorderservice.dto.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.example.msorderservice.config.RabbitMqConstants;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProducer implements RabbitMqConstants {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderEvent event) {
        publish(RabbitMqConstants.ROUTING_ORDER_CREATED, event);
    }

    public void publishOrderAssigned(OrderEvent event) {
        publish(RabbitMqConstants.ROUTING_ORDER_ASSIGNED, event);
    }

    public void publishOrderDelivered(OrderEvent event) {
        publish(RabbitMqConstants.ROUTING_ORDER_DELIVERED, event);
    }

    private void publish(String routingKey, OrderEvent event) {
        log.info("RabbitMQ: Sending event [{}] -> {}", routingKey, event);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.ORDER_EXCHANGE,
                routingKey,
                event
        );
    }
}