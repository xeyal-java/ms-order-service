package com.example.msorderservice.service;

import com.example.msorderservice.dto.OrderRequest;
import com.example.msorderservice.dto.OrderResponse;
import com.example.msorderservice.dto.criteria.OrderSearchCriteria;
import com.example.msorderservice.dto.criteria.PageResponse;
import com.example.msorderservice.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderStatus getOrderStatus(Long id);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByCustomer(Long customerId);
    void cancelOrder(Long id);
    void pickupOrder(Long id);
    void deliverOrder(Long id);
    PageResponse<OrderResponse> getOrders(OrderSearchCriteria criteria);}
