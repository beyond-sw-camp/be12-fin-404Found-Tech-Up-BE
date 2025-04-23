package com.example.backend.product.service;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.ProductImage;
import com.example.backend.product.model.dto.ProductImageSaveRequestDto;
import com.example.backend.product.repository.ProductImageRepository;
import com.example.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    public String getFileType(String filename) {
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".png")) return "image/png";
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerName.endsWith(".gif")) return "image/gif";
        if (lowerName.endsWith(".pdf")) return "application/pdf";
        if (lowerName.endsWith(".doc")) return "application/msword";
        if (lowerName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }

    public void saveFileInfo(ProductImageSaveRequestDto requestBody) throws IOException {
        Product product = productRepository.findById(requestBody.getProductIdx()).orElse(null);
        if (product == null) {
            throw new IOException("매칭되는 제품 없는 이미지");
        }
        for (String filepath: requestBody.getImagePath()) {
            ProductImage productImage = ProductImage.builder().product(product).imageUrl(filepath.split("\\?")[0]).build();
            productImageRepository.save(productImage);
        }

    }

    public String getFileKey(String filename) {
        String lowerName = filename.toLowerCase();
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/")) + UUID.randomUUID().toString() + "_" + lowerName;
    }
}
