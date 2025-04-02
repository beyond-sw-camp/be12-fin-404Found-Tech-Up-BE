package com.example.backend.user.model.dto.response;

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
public class UserInfoResponse {
    @Schema(description="DB 상의 사용자 번호")
    private Long userIdx;
    @Schema(description="사용자의 이메일")
    private String userEmail;
    @Schema(description="사용자의 별명")
    private String userNickname;
    @Schema(description="사용자 전화번호, null일 수 있음")
    private String userPhone;
    @Schema(description="사용자 주소, null일 수 있음")
    private String userAddress;
    @Schema(description="계정 생성 일자")
    private Date createdAt;
    @Schema(description="소셜 로그인 가능 여부")
    private Boolean isSocial;
    @Schema(description="위시리스트 알림 받기 설정")
    private Boolean likeEnabled;
    @Schema(description="새 상품 알림 받기 설정")
    private Boolean newEnabled;
    @Schema(description="사용자 맞춤 추천 설정")
    private Boolean upgradeEnabled;
    @Schema(description="SMS 허용 설정")
    private Boolean allowSms;
    @Schema(description="이메일 허용 설정")
    private Boolean allowEmail;
}
