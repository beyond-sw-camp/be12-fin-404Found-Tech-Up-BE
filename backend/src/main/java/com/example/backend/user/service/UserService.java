package com.example.backend.user.service;


import com.example.backend.cart.repository.CartRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.OrderResponseStatus;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.order.repository.OrderRepository;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.request.*;
import com.example.backend.user.model.dto.request.EditPwdRequestDto;
import com.example.backend.user.model.dto.request.SignupRequestDto;
import com.example.backend.user.model.dto.request.ValidateEmailRequestDto;
import com.example.backend.user.model.dto.request.VerifyNickNameRequestDto;
import com.example.backend.user.model.dto.response.*;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.wishlist.repository.WishlistRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    public void updatePwd(User loginUser, UpdatePwdRequestDto dto) {
        User user = userRepository.findByUserEmail(loginUser.getUserEmail()).orElseThrow();
        if (!passwordEncoder.matches(dto.getUserCurrentPassword(), user.getUserPassword())) {
            throw new UserException(UserResponseStatus.INVALID_PASSWORD_FAIL);
        }

        if (!dto.getUserConfirmPassword().equals(dto.getUserPassword())) {
            throw new UserException(UserResponseStatus.INVALID_PASSWORD);
        }

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

    public void deleteUser(User loginUser) {
        if (loginUser == null) {
            throw new UserException(UserResponseStatus.UNDEFINED_USER);
        }

        // 사용자 정보 조회
        User user = userRepository.findById(loginUser.getUserIdx()).orElseThrow();

        try {
            // 사용자 삭제
            userRepository.delete(user);
            log.info("User with ID: {} deleted successfully", user.getUserIdx());
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", user.getUserIdx(), e);
            throw new UserException(UserResponseStatus.USER_DELETE_FAIL);
        }
    }
    
    public Page<ReducedUserInfoDto> getAllUsersForAdmin(Integer offset, Integer limit) {
        return userRepository.findAll(PageRequest.of(offset, limit)).map(ReducedUserInfoDto::from);
    }

    public List<ReducedUserInfoDto> searchUsers(String keyword) {
        // TODO: Paging을 백엔드에서 처리할 것인가?
        List<User> target = userRepository.findAllByUserNicknameContaining(keyword);
        return target.stream().map(ReducedUserInfoDto::from).toList();
    }

    @Transactional(readOnly = true)
    public AlarmSettingResponseDto getAlarmSetting(Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new AlarmSettingResponseDto(user.getAlarmEnabled());
    }

    /**
     * 현재 로그인한 사용자의 알림 설정 변경
     */
    @Transactional
    public AlarmSettingResponseDto updateAlarmSetting(Long userIdx, AlarmSettingRequestDto request) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.setAlarmEnabled(request.getAlarmEnabled());
        userRepository.save(user);
        return new AlarmSettingResponseDto(user.getAlarmEnabled());
    }

}
