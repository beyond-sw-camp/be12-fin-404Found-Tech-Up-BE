package com.example.backend.board.repository;

import com.example.backend.board.model.Board;
import com.example.backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b " +
            "WHERE (:category IS NULL OR b.boardCategory = :category) " +
            "  AND ( " +
            "    :search IS NULL OR " +
            "    (   (:type = 'title'   AND LOWER(b.boardTitle)   LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "     OR (:type = 'content' AND LOWER(b.boardContent) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "     OR (:type = 'writer'  AND LOWER(b.user.userNickname) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "    )" +
            "  )")
    Page<Board> searchBoards(
            @Param("category") String category,
            @Param("search")   String search,
            @Param("type")     String type,
            Pageable pageable
    );

    Page<Board> findAllByUser(User user, Pageable pageable);

}
