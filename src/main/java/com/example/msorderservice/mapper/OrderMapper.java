package com.example.msorderservice.mapper;

import com.example.msorderservice.dto.OrderRequest;
import com.example.msorderservice.dto.OrderResponse;
import com.example.msorderservice.entity.Order;
import com.example.msorderservice.enums.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    Order toEntity(OrderRequest request, BigDecimal price, Long courierId, OrderStatus status);

    OrderResponse toResponse(Order order);
}