package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.dto.LikesRegisterDto;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.board.repository.LikesRepository;
import com.example.backend.comment.model.dto.CommentRegisterDto;
import com.example.backend.comment.service.CommentService;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;

    public void create(User loginUser, LikesRegisterDto dto, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx).orElseThrow();

        if (dto.getLikesType().equals(true)){
            board.addLikesCount();
        } else {
            board.addUnlikesCount();
        }

        boardRepository.save(board);
        likesRepository.save(dto.toEntity(loginUser, board));
    }
}
