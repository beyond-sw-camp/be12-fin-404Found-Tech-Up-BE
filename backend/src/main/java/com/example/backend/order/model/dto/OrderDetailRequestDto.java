// OrderItemRequestDto.java
package com.example.backend.order.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDto {
    private Long productIdx;
    private String productName;
    private Integer orderDetailQuantity;
    private int orderDetailPrice;
    private int orderDetailDiscount;
}
