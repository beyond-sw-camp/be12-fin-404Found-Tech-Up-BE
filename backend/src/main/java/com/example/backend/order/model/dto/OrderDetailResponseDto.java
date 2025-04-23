package com.example.backend.order.model.dto;

import com.example.backend.order.model.OrderDetail;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailResponseDto {
    private Long orderDetailIdx;
    private int orderDetailQuantity;
    private int orderDetailPrice;
    private int orderDetailDiscount;

    private String orderDetailName;
    private Long productIdx;

    public static OrderDetailResponseDto from(OrderDetail detail) {
        return OrderDetailResponseDto.builder()
                .orderDetailIdx(detail.getOrderDetailIdx())
                .orderDetailQuantity(detail.getOrderDetailQuantity())
                .orderDetailPrice(detail.getOrderDetailPrice())
                .orderDetailDiscount(detail.getOrderDetailDiscount())

                .orderDetailName(detail.getProduct().getName())
                .productIdx(detail.getProduct().getProductIdx())
                .build();
    }
}
