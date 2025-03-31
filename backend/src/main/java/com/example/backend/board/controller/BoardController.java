package com.example.backend.board.controller;

import com.example.backend.board.model.dto.BoardRequestDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
@Tag(name = "게시판 기능", description = "게시판 관리 API")
public class BoardController {
    private final BoardService boardService;

    @Operation(
            summary = "게시글 등록",
            description = "제목, 내용, 첨부파일과 함께 글을 작성합니다."
    )
    @PostMapping("/create")
    public void create(@RequestBody BoardRequestDto dto) {
        boardService.create(dto);
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
