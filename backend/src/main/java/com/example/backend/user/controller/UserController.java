package com.example.backend.user.controller;


import com.example.backend.common.dto.ErrorResponseDto;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.request.*;
import com.example.backend.user.model.dto.response.*;
import com.example.backend.user.service.EmailVerifyService;
import com.example.backend.user.model.dto.request.SignupRequestDto;
import com.example.backend.user.model.dto.request.UserUpdateRequestDto;
import com.example.backend.user.model.dto.request.ValidateEmailRequestDto;
import com.example.backend.user.model.dto.request.VerifyNickNameRequestDto;
import com.example.backend.user.model.dto.response.MyProfileResponseDto;
import com.example.backend.user.model.dto.response.SignupResponseDto;
import com.example.backend.user.model.dto.response.UserInfoResponseDto;
import com.example.backend.user.model.dto.response.VerifyNickNameResponseDto;
import com.example.backend.user.service.EmailVerifyService;
import com.example.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="회원 기능", description="회원 가입/회원 정보 변경 등의 작업")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmailVerifyService emailVerifyService;
    private final BaseResponseServiceImpl baseResponseService;

    @Operation(summary="닉네임 중복 확인", description = "회원 가입, 닉네임 중복 확인합니다.")
    @ApiResponse(responseCode="200", description="인증 성공, 성공 문자열을 반환합니다.")
    @ApiResponse(responseCode="400", description="인증 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/verify/nickname")
    public BaseResponse<VerifyNickNameResponseDto> verifyNickName(
            @Parameter(description="회원 가입시 닉네임 중복 확인")
            @Valid @RequestBody VerifyNickNameRequestDto request) {
        VerifyNickNameResponseDto res = userService.verifyNickName(request);
        return baseResponseService.getSuccessResponse(res, UserResponseStatus.SUCCESS );
    }

    @Operation(summary="이메일 중복 확인 및 코드 전송", description = "회원 가입시 이메일 중복 확인 및 이메일 코드 전송을 합니다")
    @ApiResponse(responseCode="200", description="인증 성공, 성공 문자열을 반환합니다.")
    @ApiResponse(responseCode="400", description="인증 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/email")
    public BaseResponse<String> sendVerificationEmail(
            @Parameter(description="회원 가입시 이메일 인증코드 전송")
            @Valid @RequestBody EmailRequestDto request) {
        emailVerifyService.sendVerificationEmail(request);
        return baseResponseService.getSuccessResponse("이메일 인증 코드가 전송되었습니다.", UserResponseStatus.SUCCESS);
    }

    @Operation(summary="이메일 인증", description = "회원 가입, 비밀번호 찾기 시 이메일 인증을 합니다")
    @ApiResponse(responseCode="200", description="인증 성공, 성공 문자열을 반환합니다.")
    @ApiResponse(responseCode="400", description="인증 실패")
    @ApiResponse(responseCode="500", description="서버 내 오류")
    @PostMapping("/verify/email")
    public BaseResponse<String> verifyEmail(
            @Parameter(description="회원 가입시 이메일 인증코드 검증")
            @Valid @RequestBody ValidateEmailRequestDto request) {
        emailVerifyService.verifyCode(request);
        return baseResponseService.getSuccessResponse("이메일 인증이 완료되었습니다.", UserResponseStatus.SUCCESS);
    }
    
    @Operation(summary="회원가입", description = "회원 가입을 합니다")
    @ApiResponse(responseCode="200", description="정상가입, 성공 문자열을 반환합니다.", content= @Content(schema = @Schema(implementation = String.class, example="Signup success")))
    @ApiResponse(responseCode="400", description="가입 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/signup")
    public BaseResponse<SignupResponseDto> signup(
            @Parameter(description="회원 가입시의 정보: User 테이블의 모든 정보를 채우지 않습니다.")
            @Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto res = userService.signup(request);
        return baseResponseService.getSuccessResponse(res, UserResponseStatus.SUCCESS );
    }

    @Operation(summary="비밀번호 찾기", description = "이메일 인증 후 비밀번호를 변경합니다")
    @ApiResponse(responseCode="200", description="변경 성공, 성공 문자열을 반환합니다.", content= @Content(schema = @Schema(implementation = String.class, example="Signup success")))
    @ApiResponse(responseCode="400", description="변경 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/edit/password")
    public BaseResponse<String> editPwd(
            @Parameter(description="비밀번호 찾기")
            @Valid @RequestBody EditPwdRequestDto request) {
        userService.editPwd(request);
        return baseResponseService.getSuccessResponse("비밀번호 변경에 성공했습니다.", UserResponseStatus.SUCCESS );
    }

    @Operation(summary="비밀번호 변경", description = "사용자의 비밀번호를 변경합니다")
    @ApiResponse(responseCode="200", description="변경 성공, 성공 문자열을 반환합니다.", content= @Content(schema = @Schema(implementation = String.class, example="Signup success")))
    @ApiResponse(responseCode="400", description="변경 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/update/password")
    public BaseResponse<String> updatePwd(
            @Parameter(description="비밀번호 찾기")
            @Valid @RequestBody UpdatePwdRequestDto request,
            @AuthenticationPrincipal User user) {
        userService.updatePwd(user, request);
        return baseResponseService.getSuccessResponse("비밀번호 변경에 성공했습니다.", UserResponseStatus.SUCCESS );
    }

    @Operation(summary="회원 정보 반환", description = "회원 정보를 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환", content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/mypage")
    private BaseResponse<UserInfoResponseDto> getMyPage(@AuthenticationPrincipal User user) {
        UserInfoResponseDto res = userService.getMyPage(user);
        return baseResponseService.getSuccessResponse(res, UserResponseStatus.SUCCESS );
    }

    @Operation(summary="회원 정보 수정", description = "회원 정보를 수정합니다")
    @ApiResponse(responseCode="200", description="정상 작업 완료", content= @Content(schema = @Schema(implementation = String.class, example="Information update success")))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/updateprofile")
    private BaseResponse<String> updateProfile(
            @Parameter(description="회원 정보 수정할 때 필요한 정보: 비밀번호를 제외하고 User 테이블의 모든 정보를 바꿉니다.")
            @AuthenticationPrincipal User user, @Valid @RequestBody UserUpdateRequestDto request) {
        userService.updateProfile(user, request);
        return baseResponseService.getSuccessResponse("information update success", UserResponseStatus.SUCCESS);
    }

    @Operation(summary="회원 정보 삭제", description = "회원 정보를 삭제(탈퇴)합니다")
    @ApiResponse(responseCode="200", description="탈퇴 성공", content= @Content(schema = @Schema(implementation = String.class, example="Good Bye!")))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @DeleteMapping("/delete")
    private BaseResponse<String> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return  baseResponseService.getSuccessResponse("User Deletes success", UserResponseStatus.SUCCESS);
    }

    @Operation(summary="로그아웃 리다이렉션", description = "로그아웃 리다이렉션")
    @ApiResponse(responseCode="200", description="로그아웃 리다이렉션용 API.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PostMapping("/logout")
    private BaseResponse<String> logout() {
        return baseResponseService.getSuccessResponse("로그아웃 성공", UserResponseStatus.SUCCESS );
    }

    @Operation(summary="로그인 확인", description = "유저 로그인 여부를 확인")
    @ApiResponse(responseCode="200", description="로그인 확인 API.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/check-auth")
    public BaseResponse<Map<String, Boolean>> checkAuth(Authentication authentication) {
        Map<String, Boolean> response = userService.chekAuth(authentication);
        return baseResponseService.getSuccessResponse(response, UserResponseStatus.SUCCESS);
    }

    @Operation(summary="로그인 상태 확인", description = "현재 유저 확인")
    @ApiResponse(responseCode="200", description="로그인 확인.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/auth/me")
    public BaseResponse<Object> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return baseResponseService.getFailureResponse(UserResponseStatus.UNDEFINED_USER);
        }
        return baseResponseService.getSuccessResponse(user, UserResponseStatus.SUCCESS);
    }

    @Operation(summary="알람 세팅 확인 ", description = "알람 세팅 ")
    @ApiResponse(responseCode="200", description="조회 성공.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/alarm")
    public BaseResponse<AlarmSettingResponseDto> getAlarmSetting(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return baseResponseService.getFailureResponse(UserResponseStatus.UNDEFINED_USER);
        }
        AlarmSettingResponseDto dto = userService.getAlarmSetting(user.getUserIdx());
        return baseResponseService.getSuccessResponse(dto, UserResponseStatus.SUCCESS);
    }

    @Operation(summary="알람 세팅 변경 ", description = "알람 세팅 변경")
    @ApiResponse(responseCode="200", description="변경 성공.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @PatchMapping("/alarm")
    public BaseResponse<AlarmSettingResponseDto> updateAlarmSetting(
            @AuthenticationPrincipal User user,
            @RequestBody AlarmSettingRequestDto request
    ) {
        if (user == null) {
            return baseResponseService.getFailureResponse(UserResponseStatus.UNDEFINED_USER);
        }
        AlarmSettingResponseDto dto = userService.updateAlarmSetting(user.getUserIdx(), request);
        return baseResponseService.getSuccessResponse(dto, UserResponseStatus.SUCCESS);
    }

    // --------------------- 여기서부터 관리자 전용 ----------------------------

    @Operation(summary="전체 회원 정보 반환", description = "회원 정보를 30개 단위로 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환", content= @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/alluser")
    private BaseResponse<Page<ReducedUserInfoDto>> getAllUser(@AuthenticationPrincipal User user,
                                                              @RequestParam Integer offset,
                                                              @RequestParam Integer limit) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        Page<ReducedUserInfoDto> userInfoList = userService.getAllUsersForAdmin(offset, limit);
        return new BaseResponseServiceImpl().getSuccessResponse(userInfoList, UserResponseStatus.SUCCESS);
    }

    @Operation(summary="검색한 회원 정보 반환", description = "키워드로 관리자 페이지에서 회원 정보를 30개 단위로 반환합니다")
    @ApiResponse(responseCode="200", description="정상 정보 반환", content= @Content(schema = @Schema(implementation = List.class), mediaType = "application/json"))
    @ApiResponse(responseCode="400", description="요청이 이상하여 실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/finduser")
    private BaseResponse<List<ReducedUserInfoDto>> searchUser(@AuthenticationPrincipal User user, @RequestParam String keyword) {
        // TODO: 서비스에서 페이징된 정보 가져오기
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        List<ReducedUserInfoDto> userInfoList = userService.searchUsers(keyword);
        return new BaseResponseServiceImpl().getSuccessResponse(userInfoList, UserResponseStatus.SUCCESS);
    }

}
