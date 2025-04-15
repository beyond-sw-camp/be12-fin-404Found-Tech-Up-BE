package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.Likes;
import com.example.backend.board.model.dto.LikesRegisterDto;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.board.repository.LikesRepository;
import com.example.backend.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void toggleLike(User loginUser, LikesRegisterDto dto, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Optional<Likes> existingLike = likesRepository.findByUserAndBoard(loginUser, board);

        if (existingLike.isPresent()) {
            Likes like = existingLike.get();
            if (like.getLikesType().equals(dto.getLikesType())) {
                // 같은 타입 클릭 → 취소
                likesRepository.delete(like);
                adjustLikeCount(board, dto.getLikesType(), -1);
            } else {
                // 다른 타입으로 변경
                adjustLikeCount(board, like.getLikesType(), -1); // 이전 값 감소
                like.setLikesType(dto.getLikesType());           // 타입 변경
                likesRepository.save(like);
                adjustLikeCount(board, dto.getLikesType(), 1);   // 새로운 값 증가
            }
        } else {
            // 처음 클릭 → 생성
            Likes newLike = Likes.builder()
                    .likesType(dto.getLikesType())
                    .user(loginUser)
                    .board(board)
                    .build();
            likesRepository.save(newLike);
            adjustLikeCount(board, dto.getLikesType(), 1);
        }

        boardRepository.save(board);
    }

    private void adjustLikeCount(Board board, boolean isLike, int delta) {
        if (isLike) {
            board.setBoardLikes((board.getBoardLikes() == null ? 0 : board.getBoardLikes()) + delta);
        } else {
            board.setBoardUnlikes((board.getBoardUnlikes() == null ? 0 : board.getBoardUnlikes()) + delta);
        }
    }
}
