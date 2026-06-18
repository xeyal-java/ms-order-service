package com.example.msorderservice.controller;

import com.example.msorderservice.dto.OrderRequest;
import com.example.msorderservice.dto.OrderResponse;
import com.example.msorderservice.dto.criteria.OrderSearchCriteria;
import com.example.msorderservice.dto.criteria.PageResponse;
import com.example.msorderservice.enums.OrderStatus;
import com.example.msorderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(OrderSearchCriteria criteria) {
        return ResponseEntity.ok(orderService.getOrders(criteria));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatus> getOrderStatus(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderStatus(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @PatchMapping("/{id}/pickup")
    public void pickupOrder(@PathVariable Long id) {
        orderService.pickupOrder(id);
    }

    @PatchMapping("/{id}/deliver")
    public void deliverOrder(@PathVariable Long id) {
        orderService.deliverOrder(id);
    }
}