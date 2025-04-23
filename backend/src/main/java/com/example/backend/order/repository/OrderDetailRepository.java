package com.example.backend.order.repository;

import com.example.backend.admin.model.TopSales;
import com.example.backend.order.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT p.productIdx productIdx, p.name productName, sum(od.orderDetailQuantity) AS number FROM OrderDetail od RIGHT JOIN Product p ON od.product = p WHERE od.orders.orderDate >= :date GROUP BY p.productIdx ORDER BY number DESC")
    List<TopSales> countTopSales(Date date);
}