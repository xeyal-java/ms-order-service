package com.example.msorderservice.entity;
import com.example.msorderservice.enums.OrderStatus;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private String pickupAddress;
    private String deliveryAddress;
    private BigDecimal price;
    private Long courierId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
