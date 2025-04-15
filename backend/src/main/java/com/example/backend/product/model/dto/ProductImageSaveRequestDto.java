package com.example.backend.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductImageSaveRequestDto {
    private Long productIdx;
    // private List<String> imageName;
    private List<String> imagePath;
    // private List<String> imageType;
}
