package com.example.msorderservice.dto;

import com.example.msorderservice.enums.OrderStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String pickupAddress;
    private String deliveryAddress;
    private BigDecimal price;
    private Long courierId;
    private OrderStatus status;
}