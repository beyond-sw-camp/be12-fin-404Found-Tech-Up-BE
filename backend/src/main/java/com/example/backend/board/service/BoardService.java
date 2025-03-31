package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.dto.BoardRequestDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public void create(BoardRequestDto dto) {
        boardRepository.save(dto.toEntity());
    }

    public List<BoardResponseDto> list() {
        List<Board> result = boardRepository.findAll();

        return result.stream().map(BoardResponseDto::from).toList();
    }

    public BoardResponseDto read(Long tempIdx) {
        Board board = boardRepository.findById(tempIdx).orElseThrow();
        return BoardResponseDto.from(board);
    }
}
