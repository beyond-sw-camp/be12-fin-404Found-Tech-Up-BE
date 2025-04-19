package com.example.backend.user.controller;

import com.example.backend.common.dto.ErrorResponseDto;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.response.MyProductResponseDto;
import com.example.backend.user.model.dto.response.UserInfoResponseDto;
import com.example.backend.user.service.UserProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="회원 제품 기능", description="회원의 개인 제품 관련 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-product")
public class UserProductController {
    private final UserProductService userProductService;
    private final BaseResponseServiceImpl baseResponseService;

    @Operation(summary="등록하기", description = "회원의 개인 제품을 등록합니다")
    @ApiResponse(responseCode="200", description="정상등록, 성공 문자열을 반환합니다.", content= @Content(schema = @Schema(implementation = String.class, example="Signup success")))
    @ApiResponse(responseCode="400", description="등록 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/register/{productIdx}")
    public BaseResponse<String> register(
            @PathVariable Long productIdx,
            @AuthenticationPrincipal User user) {
        userProductService.register(productIdx, user);
        return baseResponseService.getSuccessResponse("회원의 제품 등록에 성공했습니다.", UserResponseStatus.SUCCESS );
    }

    @Operation(summary="회원의 제품 목록 조회", description = "회원의 제품 목록을 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환", content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/my-product")
    private BaseResponse<MyProductResponseDto> myProduct(@AuthenticationPrincipal User user) {
        MyProductResponseDto res = userProductService.myProduct(user);
        return baseResponseService.getSuccessResponse(res, UserResponseStatus.SUCCESS );
    }

    @Operation(summary="회원의 제품 삭제", description = "회원이 등록한 제품을 삭제합니다")
    @ApiResponse(responseCode="200", description="삭제 성공", content= @Content(schema = @Schema(implementation = String.class, example="Good Bye!")))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @DeleteMapping("/delete/{productIdx}")
    private BaseResponse<String> delete(
            @PathVariable Long productIdx,
            @AuthenticationPrincipal User user) {
        userProductService.delete(productIdx, user);
        return  baseResponseService.getSuccessResponse("회원의 제품 삭제를 성공했습니다.", UserResponseStatus.SUCCESS);
    }
}
