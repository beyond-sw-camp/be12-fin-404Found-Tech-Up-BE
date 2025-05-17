package com.example.backend.board.model;

import com.example.backend.user.model.User;
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
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private Boolean likesType;

    @ManyToOne
    @JoinColumn(name = "board_idx")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    public void setLikesType(Boolean likesType) {
        this.likesType = likesType;
    }
}
