package com.example.backend.board.repository;

import com.example.backend.board.model.BoardFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFilesRepository extends JpaRepository<BoardFiles, Long> {
    List<BoardFiles> findByBoardIsNullAndFilesTypeAndFilesUrlContaining(String filesType, String keyword);
}
