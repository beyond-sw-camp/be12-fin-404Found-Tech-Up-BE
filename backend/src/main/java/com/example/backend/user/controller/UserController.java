package com.example.backend.user.controller;


import com.example.backend.user.model.dto.SignupRequest;
import com.example.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="회원 관련 기능", description="회원 가입/로그인")
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
    private ResponseEntity<String> signup(@Valid SignupRequest request) {
        return ResponseEntity.ok("Signup success");
    }


}
