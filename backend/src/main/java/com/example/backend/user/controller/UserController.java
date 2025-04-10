package com.example.backend.user.controller;


import com.example.backend.common.dto.ErrorResponseDto;
import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.request.SignupRequestDto;
import com.example.backend.user.model.dto.request.UserUpdateRequestDto;
import com.example.backend.user.model.dto.request.ValidateEmailRequestDto;
import com.example.backend.user.model.dto.request.VerifyNickNameRequestDto;
import com.example.backend.user.model.dto.response.UserInfoResponseDto;
import com.example.backend.user.model.dto.response.VerifyNickNameResponseDto;
import com.example.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="회원 기능", description="회원 가입/회원 정보 변경 등의 작업")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(summary="닉네임 중복 확인", description = "회원 가입, 닉네임 중복 확인합니다.")
    @ApiResponse(responseCode="200", description="인증 성공, 성공 문자열을 반환합니다.")
    @ApiResponse(responseCode="400", description="인증 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/verify/nickname")
    public ResponseEntity<VerifyNickNameResponseDto> verifyNickName(
            @Parameter(description="회원 가입시 닉네임 중복 확인")
            @Valid @RequestBody VerifyNickNameRequestDto request) {
        VerifyNickNameResponseDto dto = userService.verifyNickName(request);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary="이메일 인증", description = "회원 가입, 비밀번호 찾기 시 이메일 인증을 합니다")
    @ApiResponse(responseCode="200", description="인증 성공, 성공 문자열을 반환합니다.")
    @ApiResponse(responseCode="400", description="인증 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/verify/email")
    public ResponseEntity<String> verifyEmail(
            @Parameter(description="회원 가입시의 정보: User 테이블의 모든 정보를 채우지 않습니다.")
            @Valid @RequestBody ValidateEmailRequestDto request) {
        // TODO: Java Mail Sender 추가 후 메일 보내기
//        userService.verify(request);
        return ResponseEntity.ok("이메일을 보냈습니다. 메일함을 확인해주세요.");
    }
    
    @Operation(summary="회원가입", description = "회원 가입을 합니다")
    @ApiResponse(responseCode="200", description="정상가입, 성공 문자열을 반환합니다.", content= @Content(schema = @Schema(implementation = String.class, example="Signup success")))
    @ApiResponse(responseCode="400", description="가입 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Parameter(description="회원 가입시의 정보: User 테이블의 모든 정보를 채우지 않습니다.")
            @Valid @RequestBody SignupRequestDto request) {
        userService.signup(request);
        return ResponseEntity.ok("Signup success");
    }

    @Operation(summary="회원 정보 반환", description = "회원 정보를 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환", content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/mypage")
    private ResponseEntity<UserInfoResponseDto> getMyPage(@AuthenticationPrincipal User user) {
        // TODO: 서비스에서 정보 가져오기
        return ResponseEntity.ok(new UserInfoResponseDto());
    }

    @Operation(summary="회원 정보 수정", description = "회원 정보를 수정합니다")
    @ApiResponse(responseCode="200", description="정상 작업 완료", content= @Content(schema = @Schema(implementation = String.class, example="Information update success")))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PutMapping("/updateprofile")
    private ResponseEntity<String> updateProfile(
            @Parameter(description="회원 정보 수정할 때 필요한 정보: 비밀번호를 제외하고 User 테이블의 모든 정보를 바꿉니다.")
            @AuthenticationPrincipal User user, @Valid @RequestBody UserUpdateRequestDto request) {
        return ResponseEntity.ok("information update success");
    }

    @Operation(summary="회원 정보 삭제", description = "회원 정보를 삭제(탈퇴)합니다")
    @ApiResponse(responseCode="200", description="탈퇴 성공", content= @Content(schema = @Schema(implementation = String.class, example="Good Bye!")))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @DeleteMapping("/delete")
    private ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok("Good Bye!");
    }

    @Operation(summary="로그아웃 리다이렉션", description = "로그아웃 리다이렉션")
    @ApiResponse(responseCode="200", description="로그아웃 리다이렉션용 API.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/logout")
    private ResponseEntity<String> logout() {
        return ResponseEntity.ok("Bye and see you again!");
    }

    // --------------------- 여기서부터 관리자 전용 ----------------------------

    @Operation(summary="전체 회원 정보 반환", description = "회원 정보를 30개 단위로 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환", content= @Content(schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/alluser")
    private ResponseEntity<List<UserInfoResponseDto>> getAllUser(@AuthenticationPrincipal User user, Integer offset) {
        // TODO: 서비스에서 페이징된 정보 가져오기
        return ResponseEntity.ok(List.of());
    }

    @Operation(summary="검색한 회원 정보 반환", description = "키워드로 회원 정보를 30개 단위로 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환", content= @Content(schema = @Schema(implementation = List.class), mediaType = "application/json"))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/finduser")
    private ResponseEntity<List<UserInfoResponseDto>> searchUser(@AuthenticationPrincipal User user, String keyword, Integer offset) {
        // TODO: 서비스에서 페이징된 정보 가져오기
        return ResponseEntity.ok(List.of());
    }

}
