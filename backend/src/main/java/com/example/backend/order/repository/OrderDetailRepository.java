package com.example.backend.order.repository;

import com.example.backend.order.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    Long countOrderDetailByOrderDetailIdx(Long orderDetailIdx);
}
