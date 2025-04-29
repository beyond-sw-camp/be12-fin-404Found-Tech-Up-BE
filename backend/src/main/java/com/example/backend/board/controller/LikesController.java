package com.example.backend.board.controller;

import com.example.backend.board.model.dto.LikesRegisterDto;
import com.example.backend.board.service.LikesService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "좋아요 기능", description = "게시글의 좋아요/싫어요 기능")
@RequiredArgsConstructor
@RestController
@RequestMapping("/likes")
public class LikesController {

    private final LikesService likesService;

    private final BaseResponseService baseResponseService;

    @Operation(summary = "좋아요/싫어요 토글", description = "좋아요/싫어요를 생성, 변경, 취소하는 기능입니다.")
    @PostMapping("/toggle/{boardIdx}")
    public BaseResponse<Object> toggleLike(
            @AuthenticationPrincipal User loginUser,
            @RequestBody LikesRegisterDto dto,
            @PathVariable Long boardIdx
    ) {
        likesService.toggleLike(loginUser, dto, boardIdx);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.SUCCESS);
    }
}
