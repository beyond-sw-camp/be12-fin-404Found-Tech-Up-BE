package com.example.backend.board.controller;

import com.example.backend.board.model.dto.BoardRequestDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/create")
    public void create(@RequestBody BoardRequestDto dto) {
        boardService.create(dto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<BoardResponseDto>> list() {
        List<BoardResponseDto> response = boardService.list();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tempIdx}")
    public ResponseEntity<BoardResponseDto> read(@PathVariable Long tempIdx) {
        BoardResponseDto response = boardService.read(tempIdx);

        return ResponseEntity.ok(response);
    }
}
