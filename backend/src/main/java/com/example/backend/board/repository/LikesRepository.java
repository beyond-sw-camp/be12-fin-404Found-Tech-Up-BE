package com.example.backend.board.repository;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.Likes;
import com.example.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserAndBoard(User user, Board board);
}
