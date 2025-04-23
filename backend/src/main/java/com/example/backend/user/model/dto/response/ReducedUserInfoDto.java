package com.example.backend.user.model.dto.response;

import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="사용자 정보(관리자 조회용)")
public class ReducedUserInfoDto {
    @Schema(description="DB 상의 사용자 번호", example="1")
    private Long userIdx;
    @Schema(description="사용자의 이메일", example="example@example.com")
    private String userEmail;
    @Schema(description="사용자의 별명", example="yippie")
    private String userNickname;
    @Schema(description="사용자 전화번호, null일 수 있음", example="010-1234-1234")
    private Integer userPosts;
    @Schema(description="사용자 주소, null일 수 있음", example="서울특별시 종로구 청와대로 1")
    private Integer userReviews;

    public static ReducedUserInfoDto from(User user) {
        return ReducedUserInfoDto.builder()
                .userIdx(user.getUserIdx())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userPosts(0) // TODO: user.getPosts() 가 생기면 size를 삽입
                .userReviews(user.getReviews().size())
                .build();
    }
}
