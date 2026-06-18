package com.example.msorderservice.config;

public interface RabbitMqConstants {
    String ROUTING_ORDER_CREATED = "order.created";
    String ROUTING_ORDER_ASSIGNED = "order.assigned";
    String ROUTING_ORDER_DELIVERED = "order.delivered";
}
