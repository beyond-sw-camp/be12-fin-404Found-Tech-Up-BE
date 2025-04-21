package com.example.backend.order.repository;

import com.example.backend.admin.model.TopSales;
import com.example.backend.order.model.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT p.productIdx productIdx, p.brand productName, sum(od.orderDetailQuantity) number FROM OrderDetail od RIGHT JOIN Product p ON od.product = p WHERE od.orders.orderDate >= :date GROUP BY p.productIdx ORDER BY number DESC")
    Slice<TopSales> countTopSales(Date date, Pageable page);
}