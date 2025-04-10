package com.example.backend.common.s3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;  // bucketName 대신

    /**
     * S3에서 단일 파일 삭제
     * @param key 삭제할 파일의 키 (board_files 테이블의 key와 동일)
     */
    public void deleteFile(String key) {
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
                .map(k -> ObjectIdentifier.builder().key(k).build())
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objects).build())
                .build();

        s3Client.deleteObjects(deleteObjectsRequest);
        System.out.println("S3 파일 삭제 완료: " + keys);
    }
}