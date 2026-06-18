package com.example.msorderservice.consumer;

import com.example.msorderservice.dto.event.OrderEvent;
import com.example.msorderservice.dto.event.PaymentSuccessEvent;
import com.example.msorderservice.enums.OrderStatus;
import com.example.msorderservice.repository.OrderRepository;
import com.example.msorderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentConsumer {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @RabbitListener(queues = "payment-success-queue")
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Message received: Payment successful, Order ID: {}", event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            log.info("Order ID: {} status updated to PAID!", order.getId());
        });
    }

    @RabbitListener(queues = "order.pickup.queue")
    public void handleOrderPickup(OrderEvent event) {
        orderService.pickupOrder(event.getOrderId());
    }

    @RabbitListener(queues = "order.delivered.queue")
    public void handleOrderDelivered(OrderEvent event) {
        orderService.deliverOrder(event.getOrderId());
    }
}