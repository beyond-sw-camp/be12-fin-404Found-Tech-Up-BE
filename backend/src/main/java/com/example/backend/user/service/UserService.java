package com.example.backend.user.service;


import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.request.SignupRequestDto;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequestDto dto) {
        if (dto.getUserConfirmPassword().equals( dto.getUserPassword())){
            User user = userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getUserPassword())));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 소셜 로그인 관련 이슈 해결
        User user = userRepository.findByUserEmail(username).orElse(null);
        // TODO: 예외처리
        return user;
    }
}
