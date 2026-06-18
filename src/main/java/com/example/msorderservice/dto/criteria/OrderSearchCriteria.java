package com.example.msorderservice.dto.criteria;

import com.example.msorderservice.enums.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderSearchCriteria extends PageCriteria {
    private OrderStatus status;
    private Long customerId;
}
