package com.example.backend.admin.service;

import com.example.backend.admin.model.StatisticsResponseDto;
import com.example.backend.admin.model.TopWishListDto;
import com.example.backend.order.model.Orders;
import com.example.backend.order.repository.OrderDetailRepository;
import com.example.backend.order.repository.OrderRepository;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public StatisticsResponseDto getStatistics() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        Date criterionDate = new Date(startDate.toEpochDay());
        List<Orders> totalOrder = orderRepository.findAllByOrderDateAfter(new Date(startDate.toEpochDay()));
        double totalSales = 0.0;
        for (Orders order : totalOrder) {
            totalSales += order.getOrderTotalPrice();
        }
        List<TopWishListDto> topw = wishlistRepository.countWishlistGroupByProduct();
        Integer newcomers = userRepository.findAllByCreatedAtAfter(startDate.atStartOfDay()).size();
        Integer totalRefunds = orderRepository.findAllByOrderStatusAndOrderDateAfter("취소됨", new Date(startDate.toEpochDay())).size();
        return StatisticsResponseDto.builder().totalOrders(totalOrder.size()).totalSales(totalSales).totalRefunds(totalRefunds).topWishList(topw).newCustomers(newcomers).build();
    }

    public Integer getTotalOrders() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        List<Orders> totalOrder = orderRepository.findAllByOrderDateAfter(new Date(startDate.toEpochDay()));
        return totalOrder.size();
    }
    public Double getTotalSales() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        List<Orders> totalOrder = orderRepository.findAllByOrderDateAfter(new Date(startDate.toEpochDay()));
        double totalSales = 0.0;
        for (Orders order : totalOrder) {
            totalSales += order.getOrderTotalPrice();
        }
        return totalSales;
    }

    public Integer getNewUsers() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        return userRepository.findAllByCreatedAtAfter(startDate.atStartOfDay()).size();
    }

    public Integer getTotalRefunds() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        return orderRepository.findAllByOrderStatusAndOrderDateAfter("취소됨", new Date(startDate.toEpochDay())).size();
    }

    // TODO: view 기록하는 기능 추가 후 구현
    /*
    public List<ProductResponseDto> getTopViews() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);

        return List.of();
    }

    */
    /*
    public List<ProductResponseDto> getTopSales() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        //List<OrderDetail> orderDetails = orderDetailRepository.
    }
    */
    public List<TopWishListDto> getTopWishList() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        return wishlistRepository.countWishlistGroupByProduct();
    }


}
