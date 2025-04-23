package com.example.backend.user.model.dto.response;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="사용자 정보(비밀번호 제외)")
public class UserInfoResponseDto {
    @Schema(description="DB 상의 사용자 번호", example="1")
    private Long userIdx;
    @Schema(description="사용자의 이메일", example="example@example.com")
    private String userEmail;
    @Schema(description="사용자의 별명", example="yippie")
    private String userNickname;
    @Schema(description="사용자 전화번호, null일 수 있음", example="010-1234-1234")
    private String userPhone;
    @Schema(description="사용자 주소, null일 수 있음", example="서울특별시 종로구 청와대로 1")
    private String userAddress;
//    @Schema(description="계정 생성 일자", example="2025-04-01 16:00")
//    private Date createdAt;
//    @Schema(description="소셜 로그인 가능 여부", example="false")
//    private Boolean isSocial;
//    @Schema(description="위시리스트 알림 받기 설정", example="true")
//    private Boolean likeEnabled;
//    @Schema(description="새 상품 알림 받기 설정", example="true")
//    private Boolean newEnabled;
//    @Schema(description="사용자 맞춤 추천 설정", example="true")
//    private Boolean upgradeEnabled;
//    @Schema(description="SMS 허용 설정", example="true")
//    private Boolean allowSms;
//    @Schema(description="이메일 허용 설정", example="true")
//    private Boolean allowEmail;

    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
                .userIdx(user.getUserIdx())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userPhone(user.getUserPhone())
                .userAddress(user.getUserAddress())
                .build();
    }
}
