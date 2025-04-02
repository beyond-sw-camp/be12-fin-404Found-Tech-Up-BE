package com.example.backend.comment.controller;

import com.example.backend.comment.model.dto.CommentRegisterDto;
import com.example.backend.comment.service.CommentService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시판에 댓글을 작성하는 기능입니다.")
    @PostMapping("/create/{boardIdx}")
    public void create(@AuthenticationPrincipal User loginUser, @RequestBody CommentRegisterDto dto, @PathVariable Long boardIdx) {
        commentService.create(loginUser, dto, boardIdx);
    }
}
