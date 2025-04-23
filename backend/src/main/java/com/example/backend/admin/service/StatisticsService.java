package com.example.backend.admin.service;

import com.example.backend.admin.model.StatisticsResponseDto;
import com.example.backend.admin.model.TopSales;
import com.example.backend.admin.model.TopWishList;
import com.example.backend.order.model.OrderDetail;
import com.example.backend.order.model.Orders;
import com.example.backend.order.repository.OrderDetailRepository;
import com.example.backend.order.repository.OrderRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
        List<TopSales> tops = orderDetailRepository.countTopSales(new Date(startDate.toEpochDay()));
        List<TopWishList> topw = wishlistRepository.countWishlistGroupByProduct();
        Integer newcomers = userRepository.findAllByCreatedAtAfter(startDate.atStartOfDay()).size();
        Integer totalRefunds = getTotalRefunds();
        List<Integer> incomeData = getRecentEarningList();
        List<String> incomeXAxis = getIncomeGraphXAxis(today);
        return StatisticsResponseDto.builder()
                .totalOrders(totalOrder.size())
                .totalSales(totalSales)
                .totalRefunds(totalRefunds)
                .topSales(tops)
                .topWishList(topw)
                .newCustomers(newcomers)
                .incomeData(incomeData).build();
    }

    public Integer getTotalOrders() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate1 = LocalDate.of(year, month, 1);
        List<Orders> totalOrder = orderRepository.findAllByOrderDateAfter(new Date(startDate1.toEpochDay()));
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
        return orderRepository.findAllByOrderStatusAndOrderDateAfter("CANCELED", new Date(startDate.toEpochDay())).size()
                + orderRepository.findAllByOrderStatusAndOrderDateAfter("REFUND_REQUESTED", new Date(startDate.toEpochDay())).size();
    }

    public List<TopSales> getTopSales() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        return orderDetailRepository.countTopSales(new Date(startDate.toEpochDay()));
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
    public List<TopWishList> getTopWishList() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        return wishlistRepository.countWishlistGroupByProduct();
    }

    // 최근 3달의 월간 수입 가져오기
    public List<Integer> getRecentEarningList() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        ZonedDateTime startDate3 = ZonedDateTime.of(year, month, 1, 0,0,0,0, ZoneId.systemDefault());
        ZonedDateTime endDate3 = startDate3.plusMonths(1).minusSeconds(1);
        ZonedDateTime startDate2 = startDate3.minusMonths(1);
        ZonedDateTime endDate2 = startDate3.minusSeconds(1);
        ZonedDateTime startDate1 = startDate2.minusMonths(1);
        ZonedDateTime endDate1 = endDate2.minusSeconds(1);

        List<Orders> totalOrder1 = orderRepository.findAllByOrderDateBetween(java.sql.Timestamp.valueOf(startDate1.toLocalDateTime()), java.sql.Timestamp.valueOf(endDate1.toLocalDateTime()));
        List<Orders> totalOrder2 = orderRepository.findAllByOrderDateBetween(java.sql.Timestamp.valueOf(startDate2.toLocalDateTime()), java.sql.Timestamp.valueOf(endDate2.toLocalDateTime()));
        List<Orders> totalOrder3 = orderRepository.findAllByOrderDateBetween(java.sql.Timestamp.valueOf(startDate3.toLocalDateTime()), java.sql.Timestamp.valueOf(endDate3.toLocalDateTime()));

        List<Integer> result = new ArrayList<>();
        if (totalOrder1.isEmpty()) {
            result.add(0);
        } else {
            Integer total = 0;
            for (Orders order : totalOrder1) {
                List<OrderDetail> details = order.getOrderDetails();
                for (OrderDetail orderDetail : details) {
                    total += orderDetail.getOrderDetailPrice() * orderDetail.getOrderDetailQuantity();
                }
            }
            result.add(total);
        }
        if (totalOrder2.isEmpty()) {
            result.add(0);
        } else {
            Integer total = 0;
            for (Orders order : totalOrder2) {

                List<OrderDetail> details = order.getOrderDetails();
                for (OrderDetail orderDetail : details) {
                    total += orderDetail.getOrderDetailPrice() * orderDetail.getOrderDetailQuantity();
                }
            }
            result.add(total);
        }
        Integer total = 0;
        for (Orders order : totalOrder3) {

            List<OrderDetail> details = order.getOrderDetails();
            for (OrderDetail orderDetail : details) {
                total += orderDetail.getOrderDetailPrice() * orderDetail.getOrderDetailQuantity();
            }
        }
        result.add(total);

        return result;
    }

    public List<String> getIncomeGraphXAxis(LocalDate today) {
        int month = today.getMonthValue();
        int year = today.getYear();
        ZonedDateTime startDate3 = ZonedDateTime.of(year, month, 1, 0,0,0,0, ZoneId.systemDefault());
        ZonedDateTime startDate2 = startDate3.minusMonths(1);
        ZonedDateTime startDate1 = startDate2.minusMonths(1);
        List<String> result = new ArrayList<>();
        result.add(startDate1.getYear()+ "-" +startDate1.getMonthValue());
        result.add(startDate2.getYear()+ "-" +startDate2.getMonthValue());
        result.add(startDate3.getYear()+ "-" +startDate3.getMonthValue());
        return result;
    }

}
