package com.example.backend.comment.service;

import com.example.backend.board.model.Board;
import com.example.backend.comment.model.Comment;
import com.example.backend.comment.model.dto.CommentResponseDto;
import com.example.backend.global.exception.BaseException;
import com.example.backend.global.response.responseStatus.CommentResponseStatus;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.user.model.User;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.comment.model.dto.CommentRegisterDto;
import com.example.backend.comment.repository.CommentRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void update(User loginUser, Long commentIdx, CommentRegisterDto dto) {
        Comment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new BaseException(CommentResponseStatus.COMMENT_NOT_FOUND));

        if (!comment.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new BaseException(CommentResponseStatus.COMMENT_ACCESS_DENIED);
        }

        comment.setCommentContent(dto.getCommentContent());
        // 변경 감지를 통한 업데이트
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(User loginUser, Long commentIdx) {
        Comment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new BaseException(CommentResponseStatus.COMMENT_NOT_FOUND));

        if (!comment.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new BaseException(CommentResponseStatus.COMMENT_ACCESS_DENIED);
        }

        commentRepository.delete(comment);
    }
}
