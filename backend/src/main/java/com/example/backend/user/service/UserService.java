package com.example.backend.user.service;


import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.request.SignupRequestDto;
import com.example.backend.user.model.dto.request.ValidateEmailRequestDto;
import com.example.backend.user.model.dto.request.VerifyNickNameRequestDto;
import com.example.backend.user.model.dto.response.SignupResponseDto;
import com.example.backend.user.model.dto.response.VerifyNickNameResponseDto;
import com.example.backend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public VerifyNickNameResponseDto verifyNickName(VerifyNickNameRequestDto dto) {
        // 닉네임으로 사용자 조회
        Optional<User> existingUser = userRepository.findByUserNickname(dto.getUserNickname());

        // 사용자가 존재하지 않으면 true, 존재하면 false 반환
        boolean isAvailable = !existingUser.isPresent();
        return new VerifyNickNameResponseDto(isAvailable);
    }

    public SignupResponseDto signup(SignupRequestDto dto) {
//        boolean isSuccessSignup = false;
//        if (dto.getVerifyNickname().equals(true)){
//            if(dto.getUserConfirmPassword().equals( dto.getUserPassword())){
//                userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getUserPassword())));
//                isSuccessSignup = true;
//            }
//        }
//
//        return new SignupResponseDto(isSuccessSignup);
        if (!dto.getVerifyNickname()) {
            return new SignupResponseDto(false);
        }
        if (!dto.getUserConfirmPassword().equals(dto.getUserPassword())) {
            return new SignupResponseDto(false);
        }

        User user = dto.toEntity(passwordEncoder.encode(dto.getUserPassword()));
        userRepository.save(user);
        return new SignupResponseDto(true);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 소셜 로그인 관련 이슈 해결
        User user = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        // TODO: 예외처리
        return user;
    }

}
