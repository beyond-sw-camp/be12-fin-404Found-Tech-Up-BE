package com.example.backend.user.controller;


import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.SignupRequest;
import com.example.backend.user.model.dto.UserUpdateRequest;
import com.example.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="회원 관련 기능", description="회원 가입/회원 정보 변경 등의 작업")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(summary="회원가입", description = "회원 가입을 합니다")
    @ApiResponse(responseCode="200", description="정상가입, 성공 문자열을 반환합니다.")
    @ApiResponse(responseCode="400", description="가입 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/signup")
    private ResponseEntity<String> signup(
            @Parameter(description="회원 가입시의 정보: User 테이블의 모든 정보를 채우지 않습니다.")
            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok("Signup success");
    }

    @Operation(summary="회원 정보 반환", description = "회원 정보를 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환")
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @GetMapping("/mypage")
    private ResponseEntity<String> getMyPage(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok("Dummy Information");
    }

    @Operation(summary="회원 정보 수정", description = "회원 정보를 수정합니다")
    @ApiResponse(responseCode="200", description="정상 작업 완료")
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PutMapping("/updateprofile")
    private ResponseEntity<String> updateProfile(
            @Parameter(description="회원 정보 수정할 때 필요한 정보: 비밀번호를 제외하고 User 테이블의 모든 정보를 바꿉니다.")
            @AuthenticationPrincipal User user, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok("information update success");
    }

    @Operation(summary="회원 정보 삭제", description = "회원 정보를 삭제(탈퇴)합니다")
    @ApiResponse(responseCode="200", description="탈퇴 성공")
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @DeleteMapping("/delete")
    private ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok("Good Bye!");
    }

    @Operation(summary="로그아웃 리다이렉션", description = "로그아웃 리다이렉션")
    @ApiResponse(responseCode="200", description="로그아웃 리다이렉션용 API.")
    @ApiResponse(responseCode="400", description="가입 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/logout")
    private ResponseEntity<String> logout() {
        return ResponseEntity.ok("Bye and see you again!");
    }

}
