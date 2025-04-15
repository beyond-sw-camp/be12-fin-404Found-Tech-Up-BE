package com.example.backend.product.controller;

import com.example.backend.common.s3.PreSignedUrlService;
import com.example.backend.common.s3.S3Service;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.dto.ProductImageSaveRequestDto;
import com.example.backend.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/productimage")
public class ProductImageController {
    private final PreSignedUrlService preSignedUrlService;
    private final S3Service s3Service;
    private final ProductImageService productImageService;

    @GetMapping("/presignedUrl")
    public BaseResponse<String> getPresignedUrl(@RequestParam("filename") String filename) {
        String filetype = productImageService.getFileType(filename);
        String fileKey = productImageService.getFileKey(filename);
        return new BaseResponseServiceImpl().getSuccessResponse(preSignedUrlService.generatePreSignedUrl(fileKey, filetype), CommonResponseStatus.SUCCESS);
    }

    @PostMapping
    public BaseResponse<Object> saveFileKey(@RequestBody ProductImageSaveRequestDto requestBody) {
        try {
            productImageService.saveFileInfo(requestBody);
        } catch (Exception e) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_SAVE_FAIL);
        }
        return new BaseResponseServiceImpl().getSuccessResponse(CommonResponseStatus.SUCCESS);
    }
}
