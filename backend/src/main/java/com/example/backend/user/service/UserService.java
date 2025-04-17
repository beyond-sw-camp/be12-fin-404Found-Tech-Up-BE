package com.example.backend.user.service;


import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.request.*;
import com.example.backend.user.model.dto.request.EditPwdRequestDto;
import com.example.backend.user.model.dto.request.SignupRequestDto;
import com.example.backend.user.model.dto.request.ValidateEmailRequestDto;
import com.example.backend.user.model.dto.request.VerifyNickNameRequestDto;
import com.example.backend.user.model.dto.response.ReducedUserInfoDto;
import com.example.backend.user.model.dto.response.SignupResponseDto;
import com.example.backend.user.model.dto.response.UserInfoResponseDto;
import com.example.backend.user.model.dto.response.VerifyNickNameResponseDto;
import com.example.backend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerifyService emailVerifyService;

    public VerifyNickNameResponseDto verifyNickName(VerifyNickNameRequestDto dto) {
        // 닉네임으로 사용자 조회
        Optional<User> existingUser = userRepository.findByUserNickname(dto.getUserNickname());

        // 사용자가 존재하지 않으면 true, 존재하면 false 반환
        boolean isAvailable = !existingUser.isPresent();
        return new VerifyNickNameResponseDto(isAvailable);
    }

    public SignupResponseDto signup(SignupRequestDto dto) {
        if (!dto.getVerifyNickname()) {
            throw new UserException(UserResponseStatus.NICKNAME_NOT_FOUND);
        }
        if (!dto.getUserConfirmPassword().equals(dto.getUserPassword())) {
            throw new UserException(UserResponseStatus.INVALID_PASSWORD);
        }

        // 인증 코드 검증
        EmailVerifyService.VerificationCode verificationCode = emailVerifyService.getVerificationCode(dto.getUserEmail());
        if (verificationCode == null) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_NOTFOUND);
        }

        if (Instant.now().toEpochMilli() > verificationCode.getExpiryTime()) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_EXPIRED);
        }

        if (!verificationCode.getCode().equals(dto.getInputCode())) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_FAIL);
        }

        User user = dto.toEntity(passwordEncoder.encode(dto.getUserPassword()));
        userRepository.save(user);
        emailVerifyService.cleanExpiredCodes();
        return new SignupResponseDto(true);
    }

    public void editPwd(EditPwdRequestDto dto) {
        if (!dto.getUserConfirmPassword().equals(dto.getUserPassword())) {
            throw new UserException(UserResponseStatus.INVALID_PASSWORD);
        }
        // 인증 코드 검증
        EmailVerifyService.VerificationCode verificationCode = emailVerifyService.getVerificationCode(dto.getUserEmail());
        if (verificationCode == null) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_NOTFOUND);
        }

        if (Instant.now().toEpochMilli() > verificationCode.getExpiryTime()) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_EXPIRED);
        }

        if (!verificationCode.getCode().equals(dto.getInputCode())) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_FAIL);
        }

        User user = userRepository.findByUserEmail(dto.getUserEmail()).orElseThrow();
        user.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 소셜 로그인 관련 이슈 해결
        User user = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        // TODO: 예외처리
        return user;
    }

    public Map<String, Boolean> chekAuth(Authentication authentication) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuthenticated", authentication != null && authentication.isAuthenticated());
        return response;
    }

    public UserInfoResponseDto getMyPage(User loginUser) {
        User user = userRepository.findByUserEmail(loginUser.getUserEmail()).orElseThrow();
        return UserInfoResponseDto.from(user);
    }

    public void updateProfile(User loginUser, @Valid UserUpdateRequestDto dto) {
        User user = userRepository.findByUserEmail(loginUser.getUserEmail()).orElseThrow();

        if (dto.getUserPhone().equals(user.getUserPhone())&&dto.getUserAddress().equals(user.getUserAddress())) {
            throw new UserException(UserResponseStatus.USER_UPDATE_FAIL);
        }

        user.setUserPhone(dto.getUserPhone());
        user.setUserAddress(dto.getUserAddress());
        userRepository.save(user);
    }

    public List<ReducedUserInfoDto> getAllUsersForAdmin() {
        // TODO: Paging을 백엔드에서 처리할 것인가?
        List<User> users = userRepository.findAll();
        return users.stream().map(ReducedUserInfoDto::from).toList();
    }

    public List<ReducedUserInfoDto> searchUsers(String keyword) {
        // TODO: Paging을 백엔드에서 처리할 것인가?
        List<User> target = userRepository.findAllByUserNicknameContaining(keyword);
        return target.stream().map(ReducedUserInfoDto::from).toList();
    }
}
