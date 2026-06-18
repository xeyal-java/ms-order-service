package com.example.msorderservice.service;

import com.example.msorderservice.annotation.CacheEvict;
import com.example.msorderservice.annotation.RedisCache;
import com.example.msorderservice.client.CourierClient;
import com.example.msorderservice.client.dto.CourierDTO;
import com.example.msorderservice.client.dto.PageDTO;
import com.example.msorderservice.dto.OrderRequest;
import com.example.msorderservice.dto.OrderResponse;
import com.example.msorderservice.dto.criteria.OrderSearchCriteria;
import com.example.msorderservice.dto.criteria.PageResponse;
import com.example.msorderservice.dto.event.OrderEvent;
import com.example.msorderservice.entity.Order;
import com.example.msorderservice.enums.OrderStatus;
import com.example.msorderservice.exception.OrderNotFoundException;
import com.example.msorderservice.mapper.OrderMapper;
import com.example.msorderservice.producer.OrderProducer;
import com.example.msorderservice.repository.OrderRepository;
import com.example.msorderservice.repository.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.msorderservice.enums.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CourierClient courierClient;
    private final OrderProducer orderProducer;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        BigDecimal price = request.getPrice();
        Optional<Long> courierIdOpt = findAvailableCourier();
        OrderStatus status = courierIdOpt.isPresent() ? ASSIGNED : PENDING;

        Order order = orderMapper.toEntity(request, price, courierIdOpt.orElse(null), status);
        Order savedOrder = orderRepository.save(order);

        if (courierIdOpt.isPresent()) {
            publishOrderEvents(savedOrder);
        }
        return orderMapper.toResponse(savedOrder);
    }

    private Optional<Long> findAvailableCourier() {
        log.info("Requesting available couriers...");
        try {
            PageDTO<CourierDTO> pageDTO = courierClient.getAvailableCouriers(0, 5);
            if (pageDTO != null && pageDTO.getContent() != null && !pageDTO.getContent().isEmpty()) {
                return Optional.of(pageDTO.getContent().get(0).getId());
            }
        } catch (Exception e) {
            log.warn("Courier service unreachable. No courier assigned.");
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    @CacheEvict(key = "order_by_id_")
    public void pickupOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.setStatus(PICKED_UP);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.setStatus(DELIVERED);
        orderRepository.save(order);
        log.info("Order ID: {} status changed to DELIVERED", orderId);
    }

    private void publishOrderEvents(Order order) {
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(order.getId())
                .courierId(order.getCourierId())
                .price(order.getPrice())
                .build();

        orderProducer.publishOrderCreated(orderEvent);
        orderProducer.publishOrderAssigned(orderEvent);
        log.info("Asynchronous RabbitMQ events published for Order ID: {}", order.getId());
    }

    @Override
    @RedisCache(key = "order_status_", ttl = 300)
    public OrderStatus getOrderStatus(Long id) {
        return orderRepository.findById(id)
                .map(Order::getStatus)
                .orElseThrow(() -> new OrderNotFoundException("Order ID: " + id + " not found!"));
    }

    @Override
    @RedisCache(key = "order_by_id_", ttl = 600)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findAllByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public PageResponse<OrderResponse> getOrders(OrderSearchCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.getSortDirection()), criteria.getSortBy());
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        Specification<Order> spec = OrderSpecification.build(criteria);
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        List<OrderResponse> content = orderPage.getContent().stream()
                .map(orderMapper::toResponse)
                .toList();
        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .hasNext(orderPage.hasNext())
                .build();
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
        order.setStatus(CANCELLED);
        orderRepository.save(order);
    }
}