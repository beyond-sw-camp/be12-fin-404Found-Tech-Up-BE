package com.example.backend.user.service;

import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private static class VerificationCode {
        private final String code;
        private final long expiryTime;

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
    public void sendVerificationEmail(String email) {
        // 이메일 중복 확인
        if (userRepository.findByUserEmail(email).isPresent()) {
            throw new UserException(UserResponseStatus.EMAIL_ALREADY_IN_USE);
        }
        String code = generateVerificationCode();
        long expiryTime = Instant.now().toEpochMilli() + 2 * 60 * 1000; // 2분 후 만료

        // 인증 코드 저장
        codeStore.put(email, new VerificationCode(code, expiryTime));

        // 이메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("이메일 인증 코드");
            helper.setText("인증 코드는 <b>" + code + "</b> 입니다. 2분 내에 입력해주세요.", true);
            mailSender.send(message);
        } catch (Exception e) {
            codeStore.remove(email); // 전송 실패 시 코드 제거
            throw new UserException(UserResponseStatus.EMAIL_SEND_FAIL);
        }
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String inputCode) {
        VerificationCode verificationCode = codeStore.get(email);
        if (verificationCode == null) {
            throw new RuntimeException("인증 코드가 존재하지 않습니다.");
        }

        // 만료 시간 체크
        if (Instant.now().toEpochMilli() > verificationCode.getExpiryTime()) {
            codeStore.remove(email);
            throw new RuntimeException("인증 코드가 만료되었습니다.");
        }

        // 코드 일치 여부 확인
        if (!verificationCode.getCode().equals(inputCode)) {
            return false;
        }

        // 검증 성공 시 enabled 상태 업데이트
//        User user = userRepository.findById(email)
//                .orElse(new User());
//        user.setEmail(email);
//        user.setEnabled(true);
//        userRepository.save(user);

        // 검증 완료 후 코드 제거
        codeStore.remove(email);
        return true;
    }

    // 주기적으로 만료된 코드 정리 (선택 사항)
    public void cleanExpiredCodes() {
        long currentTime = Instant.now().toEpochMilli();
        codeStore.entrySet().removeIf(entry ->
                entry.getValue().getExpiryTime() < currentTime);
    }
}
