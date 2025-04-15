package com.example.backend.board.model;

import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String boardTitle;
    @Lob
    private String boardContent;
    private String boardCategory;
    private LocalDateTime boardCreated;
    private LocalDateTime boardModified;
    private Integer boardLikes;
    private Integer boardUnlikes;
    private Integer boardComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 10)  // 한 번에 최대 10개의 이미지 조회
    private List<BoardFiles> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likesList = new ArrayList<>();

    public void addLikesCount() {
        this.boardLikes = this.boardLikes + 1;
    }
    public void addUnlikesCount() {
        this.boardUnlikes = this.boardUnlikes + 1;
    }
    public void addCommentsCount() {
        this.boardComments = this.boardComments + 1;
    }

    public void update(String boardTitle, String boardContent) {
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardModified = LocalDateTime.now();
    }
}
