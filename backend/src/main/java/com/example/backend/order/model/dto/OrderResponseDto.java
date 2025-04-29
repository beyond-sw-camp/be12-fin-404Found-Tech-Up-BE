package com.example.backend.order.model.dto;

import com.example.backend.order.model.Orders;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderResponseDto {
    private Long orderIdx;
    private double orderTotalPrice;
    private Long couponIdx;
    private double shipCost;
    private String paymentMethod;
    private String shippingMethod;
    private String orderStatus;
    private Date orderDate;
    private List<OrderDetailResponseDto> orderDetails;

    private String storeId;
    private String channelKey;

    public static OrderResponseDto from(Orders order) {
        return OrderResponseDto.builder()
                .orderIdx(order.getOrderIdx())
                .orderTotalPrice(order.getOrderTotalPrice())
                .couponIdx(order.getCouponIdx())
                .shipCost(order.getShipCost())
                .paymentMethod(order.getPaymentMethod())
                .shippingMethod(order.getShippingMethod())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .orderDetails(order.getOrderDetails().stream()
                        .map(OrderDetailResponseDto::from)
                        .collect(Collectors.toList()))
                .storeId(order.getStoreId())
                .channelKey(order.getChannelKey())
                .build();
    }
}
