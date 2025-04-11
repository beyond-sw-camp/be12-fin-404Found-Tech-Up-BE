package com.example.backend.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TopSalesDto {
    private Long productIdx;
    private String productName;
    private Integer number;
}
