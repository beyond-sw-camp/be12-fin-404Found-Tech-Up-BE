package com.example.backend.user.service;

import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.user.model.dto.request.EmailRequestDto;
import com.example.backend.user.model.dto.request.ValidateEmailRequestDto;
import com.example.backend.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailVerifyService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    // 인증 코드 저장용 Map: {email: {code, expiryTime}}
    private final Map<String, VerificationCode> codeStore = new ConcurrentHashMap<>();

    // 인증 코드 데이터 클래스
    @Getter
    @Setter
    public static class VerificationCode {
        private final String code;
        private long expiryTime;

        public VerificationCode(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
    }

    // 6자리 랜덤 코드 생성
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    // 이메일로 인증 코드 전송
    public void sendVerificationEmail(EmailRequestDto dto) {
        // 회원가입시
        if (dto.getIsSignup()) {
            // 이메일 중복 확인
            if (userRepository.findByUserEmail(dto.getUserEmail()).isPresent()) {
                throw new UserException(UserResponseStatus.EMAIL_ALREADY_IN_USE);
            }
        } else {
            // 이메일 중복 확인
            if (!userRepository.findByUserEmail(dto.getUserEmail()).isPresent()) {
                throw new UserException(UserResponseStatus.INVALID_EMAIL_FORMAT);
            }
        }
        String code = generateVerificationCode();
        long expiryTime = Instant.now().toEpochMilli() + 3 * 60 * 1000; // 2분 후 만료

        // 인증 코드 저장
        codeStore.put(dto.getUserEmail(), new VerificationCode(code, expiryTime));

        // 이메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(dto.getUserEmail());
            helper.setSubject("이메일 인증 코드");
            helper.setText("인증 코드는 <b>" + code + "</b> 입니다. 3분 내에 입력해주세요.", true);
            mailSender.send(message);
        } catch (Exception e) {
            codeStore.remove(dto.getUserEmail()); // 전송 실패 시 코드 제거
            throw new UserException(UserResponseStatus.EMAIL_SEND_FAIL);
        }
    }

    // 인증 코드 검증
    public void verifyCode(ValidateEmailRequestDto dto) {
        VerificationCode verificationCode = codeStore.get(dto.getUserEmail());
        if (verificationCode == null) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_NOTFOUND);
        }

        // 만료 시간 체크
        if (Instant.now().toEpochMilli() > verificationCode.getExpiryTime()) {
            codeStore.remove(dto.getUserEmail());
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_EXPIRED);
        }

        // 코드 일치 여부 확인
        if (!verificationCode.getCode().equals(dto.getInputCode())) {
            throw new UserException(UserResponseStatus.EMAIL_VERIFY_FAIL);
        }

        // 검증 성공 시 expiryTime을 10분으로 업데이트
        verificationCode.setExpiryTime(Instant.now().toEpochMilli() + 10 * 60 * 1000); // 10분 연장
        codeStore.put(dto.getUserEmail(), verificationCode);
    }

    // EmailVerifyService에 추가
    public VerificationCode getVerificationCode(String email) {
        return codeStore.get(email);
    }

    // 주기적으로 만료된 코드 정리 (선택 사항)
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanExpiredCodes() {
        long currentTime = Instant.now().toEpochMilli();
        codeStore.entrySet().removeIf(entry ->
                entry.getValue().getExpiryTime() < currentTime);
    }
}
