package com.example.backend.comment.repository;

import com.example.backend.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdxOrderByCommentCreatedAsc(Long boardIdx);
}
