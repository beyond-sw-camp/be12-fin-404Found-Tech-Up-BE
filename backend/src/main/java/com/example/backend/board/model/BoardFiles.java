package com.example.backend.board.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class BoardFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String filesUrl;
    private String filesType;
    private String filesName;

    @ManyToOne
    @JoinColumn(name = "board_idx")
    private Board board;

    public void linkToBoard(Board board) {
        this.board = board;
    }
}
