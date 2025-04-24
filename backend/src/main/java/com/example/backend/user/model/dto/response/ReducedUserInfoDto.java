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
    @Schema(description="사용자 포스팅 횟수, null일 수 있음", example="4")
    private Integer userPosts;
    @Schema(description="사용자 리뷰 횟수, null일 수 있음", example="1")
    private Integer userReviews;
    @Schema(description="사용자 전화번호, null일 수 있음", example="010-1234-1234")
    private String userPhone;
    @Schema(description="사용자 주소, null일 수 있음", example="서울특별시 종로구 청와대로 1")
    private String userAddress;

    public static ReducedUserInfoDto from(User user) {
        return ReducedUserInfoDto.builder()
                .userIdx(user.getUserIdx())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userPosts(user.getPosts() != null ? user.getPosts().size() : 0)
                .userReviews(user.getReviews() != null ?user.getReviews().size(): 0)
                .userPhone(user.getUserPhone())
                .userAddress(user.getUserAddress())
                .build();
    }
}
