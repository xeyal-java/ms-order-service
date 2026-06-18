package com.example.msorderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String ORDER_EXCHANGE = "order-exchange";
    public static final String PAYMENT_EXCHANGE = "payment-exchange";

    public static final String PAYMENT_SUCCESS_QUEUE = "payment-success-queue";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success.routing.key";

    public static final String PICKUP_QUEUE = "order.pickup.queue";
    public static final String DELIVERED_QUEUE = "order.delivered.queue";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue(PAYMENT_SUCCESS_QUEUE);
    }

    @Bean
    public Queue pickupQueue() {
        return new Queue(PICKUP_QUEUE);
    }

    @Bean
    public Queue deliveredQueue() {
        return new Queue(DELIVERED_QUEUE);
    }

    @Bean
    public Binding paymentBinding(Queue paymentSuccessQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentSuccessQueue).to(paymentExchange).with(PAYMENT_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}