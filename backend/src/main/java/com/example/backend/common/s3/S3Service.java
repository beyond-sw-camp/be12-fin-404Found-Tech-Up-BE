package com.example.backend.common.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에서 단일 파일 삭제
     * @param key 삭제할 파일의 키 (board_files 테이블의 key와 동일)
     */
    public void deleteFile(String key) {
        System.out.println("🗑️ S3 삭제 요청 key: {}" + key);  // 꼭 추가
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
        System.out.println("S3 파일 삭제 완료: " + key);
    }

    /**
     * S3에서 다수의 파일 삭제
     * @param keys 삭제할 파일 키 목록
     */
    public void deleteFiles(List<String> keys) {
        List<ObjectIdentifier> objects = keys.stream()
                .filter(k -> k != null && !k.trim().isEmpty()) // ❗ null 또는 빈 키 제거
                .map(k -> ObjectIdentifier.builder().key(k).build())
                .collect(Collectors.toList());

        if (objects.isEmpty()) {
            System.out.println("삭제할 S3 키가 없습니다.");
            return; // 아무것도 삭제하지 않도록 방어
        }

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objects).build())
                .build();

        s3Client.deleteObjects(deleteObjectsRequest);
        System.out.println("S3 파일 삭제 완료: " + keys);
    }

    /**
     * 프리사인드 URL을 통해 S3에 파일 업로드를 진행합니다.
     * 이 메소드는 생성된 presignedUrl로 HTTP PUT 요청을 보내어 파일을 업로드하는 로직을 포함합니다.
     *
     * @param presignedUrl 프리사인드 URL (파일 업로드 전용)
     * @param file 업로드할 파일 (java.io.File)
     * @param contentType 파일의 Content-Type (예: "image/png")
     */
    /*
    public void uploadFileWithPresignedUrl(String presignedUrl, File file, String contentType) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(presignedUrl))
                    .header("Content-Type", contentType)
                    .PUT(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                    .build();
            // HTTP PUT 요청 전송
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("S3 업로드 결과: " + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

     */

    public void uploadFileWithPresignedUrl(String presignedUrl, MultipartFile file, String contentType) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(presignedUrl).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", contentType);
            System.out.println("📦 최종 요청 Content-Type Header: " + connection.getRequestProperty("Content-Type"));
            System.out.println("🔥 Content-Type sent to S3: " + contentType);
            connection.getRequestProperties().forEach((k, v) -> System.out.println(k + ": " + v));
            try (OutputStream os = connection.getOutputStream();
                 InputStream is = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Failed to upload file to S3: " + responseCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }

}
