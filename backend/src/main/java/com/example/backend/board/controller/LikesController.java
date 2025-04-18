package com.example.backend.board.controller;

import com.example.backend.board.model.dto.LikesRegisterDto;
import com.example.backend.board.service.LikesService;
import com.example.backend.comment.model.dto.CommentRegisterDto;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/likes")
public class LikesController {
    private final LikesService likesService;

    @Operation(summary = "좋아요/싫어요", description = "게시판에 좋아요/싫어요를 클릭하는 기능입니다.")
    @PostMapping("/create/{boardIdx}")
    public void create(@AuthenticationPrincipal User loginUser, @RequestBody LikesRegisterDto dto, @PathVariable Long boardIdx) {
        likesService.create(loginUser, dto, boardIdx);
    }
}
