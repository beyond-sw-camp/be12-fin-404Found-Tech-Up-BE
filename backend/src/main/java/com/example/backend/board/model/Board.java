package com.example.backend.board.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String boardTitle;
    private String boardContent;
    private LocalDateTime boardCreated;
    private LocalDateTime boardModified;
    private Integer boardLikes;
    private Integer boardUnlikes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 10)  // 한 번에 최대 10개의 이미지 조회
    private List<BoardImage> imageList = new ArrayList<>();

    public void update(String boardTitle, String boardContent) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardModified = LocalDateTime.now();
    }
}
