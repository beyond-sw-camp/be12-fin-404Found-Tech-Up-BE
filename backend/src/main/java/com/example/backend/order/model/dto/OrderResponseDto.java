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
    private String orderStatus;
    private Date orderDate;
    private List<OrderDetailResponseDto> orderDetails;

    public static OrderResponseDto from(Orders order) {
        return OrderResponseDto.builder()
                .orderIdx(order.getOrderIdx())
                .orderTotalPrice(order.getOrderTotalPrice())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .orderDetails(order.getOrderDetails().stream()
                        .map(OrderDetailResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
