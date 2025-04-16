package com.example.backend.comment.service;

import com.example.backend.board.model.Board;
import com.example.backend.comment.model.dto.CommentResponseDto;
import com.example.backend.user.model.User;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.comment.model.dto.CommentRegisterDto;
import com.example.backend.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public void create(User loginUser, CommentRegisterDto dto, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx).orElseThrow();
        board.addCommentsCount();
        boardRepository.save(board);
        commentRepository.save(dto.toEntity(loginUser, board));
    }

    public List<CommentResponseDto> getComments(Long boardIdx) {
        return commentRepository.findByBoardIdxOrderByCommentCreatedAsc(boardIdx).stream()
                .map(CommentResponseDto::from)
                .toList();
    }
}
