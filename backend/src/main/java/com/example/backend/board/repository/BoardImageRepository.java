package com.example.backend.board.repository;

import com.example.backend.board.model.BoardFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardFiles, Long> {
}
