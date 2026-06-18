package com.example.msorderservice.repository.specification;
import com.example.msorderservice.dto.criteria.OrderSearchCriteria;
import com.example.msorderservice.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> build(OrderSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.getStatus()));
            }
            if (criteria.getCustomerId() != null) {
                predicates.add(builder.equal(root.get("customerId"), criteria.getCustomerId()));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
