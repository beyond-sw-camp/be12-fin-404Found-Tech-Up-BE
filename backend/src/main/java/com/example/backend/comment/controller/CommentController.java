package com.example.backend.comment.controller;

import com.example.backend.comment.model.dto.CommentRegisterDto;
import com.example.backend.comment.model.dto.CommentResponseDto;
import com.example.backend.comment.service.CommentService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 기능", description = "게시판의 댓글 관련 기능을 제공합니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "댓글 작성", description = "게시판에 댓글을 작성하는 기능입니다.")
    @PostMapping("/create/{boardIdx}")
    public BaseResponse<Object> create(@AuthenticationPrincipal User loginUser, @RequestBody CommentRegisterDto dto, @PathVariable Long boardIdx) {
        commentService.create(loginUser, dto, boardIdx);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.CREATED);
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글에 작성된 댓글들을 조회합니다.")
    @GetMapping("/list/{boardIdx}")
    public BaseResponse<Object> getComments(@PathVariable Long boardIdx) {
        List<CommentResponseDto> commentList = commentService.getComments(boardIdx);
        return baseResponseService.getSuccessResponse(commentList, CommonResponseStatus.SUCCESS);
    }

    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다.")
    @PatchMapping("/update/{commentIdx}")
    public BaseResponse<Object> updateComment(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long commentIdx,
            @RequestBody CommentRegisterDto dto
    ) {
        commentService.update(loginUser, commentIdx, dto);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.UPDATED);
    }

    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    @DeleteMapping("/delete/{commentIdx}")
    public BaseResponse<Object> deleteComment(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long commentIdx
    ) {
        commentService.delete(loginUser, commentIdx);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.DELETED);
    }



}
