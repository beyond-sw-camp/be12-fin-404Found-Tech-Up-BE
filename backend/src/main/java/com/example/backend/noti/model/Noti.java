package com.example.backend.noti.model;

import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Noti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String notiTitle;
    private String notiContent;
    // True: 전체, False: 개인
    private Boolean notiType;
    private LocalDateTime notiCreated;
    private LocalDateTime notiChecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;
}
