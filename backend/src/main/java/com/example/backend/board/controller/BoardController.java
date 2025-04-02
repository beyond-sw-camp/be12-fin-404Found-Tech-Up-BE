package com.example.backend.board.controller;

import com.example.backend.board.model.dto.BoardRegisterDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.service.BoardService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
@Tag(name = "게시판 기능", description = "게시판 관리 API")
public class BoardController {
    private final BoardService boardService;

    @Operation(
            summary = "게시글 등록",
            description = "제목, 내용, 첨부파일과 함께 글을 작성합니다."
    )
    @PostMapping("/create")
    public void create(@AuthenticationPrincipal User loginUser, @RequestBody BoardRegisterDto dto) {
        boardService.create(loginUser, dto);
    }

    @Operation(
            summary = "게시글 수정",
            description = "boardIdx를 전달받아 본인이 작성한글인지 확인 후, 게시글의 제목과 내용, 첨부파일을 수정합니다."
    )
    @PostMapping("/update/{boardIdx}")
    public void update(@AuthenticationPrincipal User loginUser, @PathVariable Long boardIdx, @RequestBody BoardRegisterDto dto) {
        boardService.update(loginUser, boardIdx, dto);
    }

    @Operation(
            summary = "게시글 삭제",
            description = "boardIdx를 전달받아 본인이 작성한 글인지 확인 후, 해당 게시글을 삭제합니다."
    )
    @DeleteMapping("/delete/{boardIdx}")
    public void delete(@AuthenticationPrincipal User loginUser, @PathVariable Long boardIdx) {
        boardService.delete(loginUser, boardIdx);
    }

    @Operation(
            summary = "게시글 리스트보기",
            description = "게시글 목록을 확인합니다."
    )
    @GetMapping("/list")
    public ResponseEntity<List<BoardResponseDto>> list() {
        List<BoardResponseDto> response = boardService.list();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "게시글 상세보기",
            description = "boardIdx를 전달받아 게시글 하나의 정보를 확인합니다."
    )
    @GetMapping("/read/{boardIdx}")
    public ResponseEntity<BoardResponseDto> read(@PathVariable Long boardIdx) {
        BoardResponseDto response = boardService.read(boardIdx);

        return ResponseEntity.ok(response);
    }
}
