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
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping("/upload")
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_SAVE_FAIL);
        }
        String filetype = productImageService.getFileType(file.getOriginalFilename());
        String fileKey = productImageService.getFileKey(file.getOriginalFilename());
        String url = preSignedUrlService.generatePreSignedUrl(fileKey, filetype);
        s3Service.uploadFileWithPresignedUrl(url,file,filetype);
        return new BaseResponseServiceImpl().getSuccessResponse(url, CommonResponseStatus.SUCCESS);
    }

    @PostMapping
    public BaseResponse<String> saveFileKey(@RequestBody ProductImageSaveRequestDto requestBody) {
        try {
            productImageService.saveFileInfo(requestBody);
        } catch (Exception e) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_SAVE_FAIL);
        }
        return new BaseResponseServiceImpl().getSuccessResponse("성공", CommonResponseStatus.SUCCESS);
    }
}
