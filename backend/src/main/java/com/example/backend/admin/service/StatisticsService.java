package com.example.backend.admin.service;

import com.example.backend.admin.model.dto.StatisticsResponseDto;
import com.example.backend.admin.model.TopSales;
import com.example.backend.admin.model.dto.TopSalesResponseDto;
import com.example.backend.admin.model.TopWishList;
import com.example.backend.admin.model.dto.TopWishlistResponseDto;
import com.example.backend.global.exception.ProductException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.order.model.OrderDetail;
import com.example.backend.order.model.Orders;
import com.example.backend.order.repository.OrderDetailRepository;
import com.example.backend.order.repository.OrderRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.review.model.Review;
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
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public StatisticsResponseDto getStatisticsEtc() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        List<Orders> totalOrder = orderRepository.findAllByOrderDateAfter(new Date(startDate.toEpochDay()));
        double totalSales = 0.0;
        for (Orders order : totalOrder) {
            totalSales += order.getOrderTotalPrice();
        }
        Integer newcomers = userRepository.findAllByCreatedAtAfter(startDate.atStartOfDay()).size();
        return StatisticsResponseDto.builder()
                .totalSales(totalSales)
                .newCustomers(newcomers)
                .build();
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

    public List<TopSalesResponseDto> getTopSales() {
        LocalDate theDay = LocalDate.now().minusMonths(1);
        int month = theDay.getMonthValue();
        int year = theDay.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        List<TopSales> topSales = orderDetailRepository.countTopSales(new Date(startDate.toEpochDay()));
        List<TopSalesResponseDto> result = new ArrayList<>();
        for (TopSales item : topSales) {
            Product product = productRepository.findById(item.getProductIdx()).orElseThrow(()-> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
            String productImageUrl = product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0).getImageUrl(): "";
            List<Integer> reviews = product.getReviews() != null && !product.getReviews().isEmpty() ? product.getReviews().stream().map(Review::getReviewRating).toList() : new ArrayList<>();
            result.add(TopSalesResponseDto.builder().productIdx(item.getProductIdx()).productImageUrl(productImageUrl).productName(product.getName()).productPrice(product.getPrice()).productDiscount(product.getDiscount()).reviews(reviews).build());
        }
        return result;
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

    public List<TopWishlistResponseDto> getTopWishList() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        List<TopWishList> wishLists = wishlistRepository.countWishlistGroupByProduct();
        List<TopWishlistResponseDto> result = new ArrayList<>();
        for (TopWishList item : wishLists) {
            Product product = productRepository.findById(item.getProductIdx()).orElseThrow(()-> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
            String productImageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ? product.getImages().get(0).getImageUrl(): "";
            List<Integer> reviews = product.getReviews() != null && !product.getReviews().isEmpty() ? product.getReviews().stream().map(Review::getReviewRating).toList() : new ArrayList<>();
            result.add(TopWishlistResponseDto.builder().productIdx(product.getProductIdx()).productName(product.getName()).productDiscount(product.getDiscount()).price(product.getPrice()).brand(product.getBrand()).imageUrl(productImageUrl).reviews(reviews).cw(item.getCw()).build());
        }
        return result;
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
