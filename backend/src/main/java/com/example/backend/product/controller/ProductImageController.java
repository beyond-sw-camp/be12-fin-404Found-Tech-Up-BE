package com.example.backend.product.controller;

import com.example.backend.common.dto.ErrorResponseDto;
import com.example.backend.common.s3.PreSignedUrlService;
import com.example.backend.common.s3.S3Service;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.dto.ProductImageSaveRequestDto;
import com.example.backend.product.service.ProductImageService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary="S3 Presigned", description = "S3 Presigned URL 발급")
    @ApiResponse(responseCode="200", description="성공.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/presignedUrl")
    public BaseResponse<String> getPresignedUrl(@AuthenticationPrincipal User user, @RequestParam("filename") String filename) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        String filetype = productImageService.getFileType(filename);
        String fileKey = productImageService.getFileKey(filename);
        return new BaseResponseServiceImpl().getSuccessResponse(preSignedUrlService.generatePreSignedUrl(fileKey, filetype), CommonResponseStatus.SUCCESS);
    }

    @Operation(summary="파일 URL 업로드", description = "파일 URL 등록 ")
    @ApiResponse(responseCode="200", description="등록 성공 .", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PutMapping("/upload")
    public BaseResponse<String> upload(@AuthenticationPrincipal User user,@RequestParam("file") MultipartFile file) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_SAVE_FAIL);
        }
        String filetype = productImageService.getFileType(file.getOriginalFilename());
        String fileKey = productImageService.getFileKey(file.getOriginalFilename());
        String url = preSignedUrlService.generatePreSignedUrl(fileKey, filetype);
        s3Service.uploadFileWithPresignedUrl(url,file,filetype);
        return new BaseResponseServiceImpl().getSuccessResponse(url, CommonResponseStatus.SUCCESS);
    }

    @Operation(summary="파일 키 저장 ", description = "파일 키 저장")
    @ApiResponse(responseCode="200", description="로그인 확인 API.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping
    public BaseResponse<String> saveFileKey(@AuthenticationPrincipal User user, @RequestBody ProductImageSaveRequestDto requestBody) {
        try {
            if (user == null || !user.getIsAdmin()) {
                return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
            }
            productImageService.saveFileInfo(requestBody);
        } catch (Exception e) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_SAVE_FAIL);
        }
        return new BaseResponseServiceImpl().getSuccessResponse("성공", CommonResponseStatus.SUCCESS);
    }
}
