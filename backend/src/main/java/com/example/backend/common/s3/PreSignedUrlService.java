package com.example.backend.common.s3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PreSignedUrlService {

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;  // bucketName 대신

    /**
     * 파일 업로드를 위한 presigned URL 생성
     * @param key 업로드할 파일의 키 (예: board_files/{파일명})
     * @param contentType 파일의 콘텐츠 타입
     * @return 프리사인드 URL 문자열
     */
    public String generatePreSignedUrl(String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(b -> b
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build()
        );

        // ✅ 확인용 로그 추가
        System.out.println("📎 Presigned URL: " + presignedRequest.url());
        System.out.println("📎 Signed Headers: " + presignedRequest.signedHeaders());

        return presignedRequest.url().toString();
    }

}
