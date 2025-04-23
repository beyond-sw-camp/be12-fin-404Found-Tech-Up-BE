package com.example.backend.order.repository;

import com.example.backend.order.model.Orders;
import com.example.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByOrderDateAfter(Date date);
    List<Orders> findAllByOrderStatusAndOrderDateAfter(String status, Date date);

    List<Orders> findAllByUserOrderByOrderDateDesc(User user);
    List<Orders> findAllByOrderDateBetween(Date date1, Date date2);

    @Query("""
    select o
      from Orders o
      left join fetch o.orderDetails d
      left join fetch d.product p
     where o.user.userIdx = :userIdx
    """)
    List<Orders> findAllByUserWithDetails(@Param("userIdx") Long userIdx);
}
