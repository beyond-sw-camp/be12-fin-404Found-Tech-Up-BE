package com.example.backend.order.repository;

import com.example.backend.order.model.Orders;
import com.example.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByOrderDateAfter(Date date);
    List<Orders> findAllByOrderStatusAndOrderDateAfter(String status, Date date);

    List<Orders> findAllByUserOrderByOrderDateDesc(User user);
}
