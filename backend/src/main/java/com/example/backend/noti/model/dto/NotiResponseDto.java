package com.example.backend.noti.model.dto;

import com.example.backend.noti.model.Noti;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotiResponseDto {
    @Schema(description = "알림의 고유 아이디", example = "1")
    private Long idx;
    @Schema(description = "알림 제목", example = "알림 제목입니다.")
    private String notiTitle;
    @Schema(description = "알림 내용", example = "알림 내용입니다.")
    private String notiContent;
    @Schema(description = "알림 대상", example = "사용자 이름")
    private String writer;
    @Schema(description = "알림 생성 시간", example = "2025-03-05")
    private LocalDateTime notiCreated;
    @Schema(description = "알림 확인 여부", example = "False")
    private LocalDateTime notiChecked;

    public static NotiResponseDto from(Noti noti) {
        return NotiResponseDto.builder()
                .idx(noti.getIdx())
                .notiTitle(noti.getNotiTitle())
                .notiContent(noti.getNotiContent())
//                .writer(board.getUser().getName())
                .notiCreated(noti.getNotiCreated())
                .notiChecked(noti.getNotiChecked())
                .build();
    }
}
