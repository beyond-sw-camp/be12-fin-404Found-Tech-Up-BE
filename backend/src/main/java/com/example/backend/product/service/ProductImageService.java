package com.example.backend.product.service;

import com.example.backend.common.s3.S3Service;
import com.example.backend.product.model.Product;
import com.example.backend.product.model.ProductImage;
import com.example.backend.product.model.dto.ProductImageSaveRequestDto;
import com.example.backend.product.repository.ProductImageRepository;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.util.HttpClientUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final S3Service s3Service;

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
    @Transactional
    public void saveFileInfo(ProductImageSaveRequestDto requestBody) throws IOException {
        Product product = productRepository.findById(requestBody.getProductIdx()).orElse(null);
        if (product == null) {
            throw new IOException("매칭되는 제품 없는 이미지");
        }
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<String> keys = product.getImages().stream().map(image -> getFileKeyForDelete(image.getImageUrl())).toList();
            productImageRepository.deleteAll(product.getImages());
            s3Service.deleteFiles(keys);
        }
        // 업데이트를 할 경우를 대비하여 기존 이미지 목록과 비교
        for (String filepath: requestBody.getImagePath()) {
            ProductImage productImage = ProductImage.builder().product(product).imageUrl(filepath.split("\\?")[0]).build();
            productImageRepository.save(productImage);
        }

    }

    public String getFileKey(String filename) {
        String lowerName = filename.toLowerCase();
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/")) + UUID.randomUUID().toString() + "_" + lowerName;
    }

    // 제품 이미지 삭제는 ProductService에서 한다.
    // 대신 key를 뽑는다.
    public String getFileKeyForDelete(String urlString) {
        log.debug("File Key: {}",urlString.split("com/")[1]);
        return urlString.split("com/")[1];
    }
}
