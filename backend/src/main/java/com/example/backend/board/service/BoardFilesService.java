package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.BoardFiles;
import com.example.backend.board.model.dto.BoardFilesRequestDto;
import com.example.backend.board.repository.BoardFilesRepository;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.common.s3.PreSignedUrlService;
import com.example.backend.common.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardFilesService {

    private final PreSignedUrlService preSignedUrlService;
    private final S3Service s3Service;
    private final BoardFilesRepository boardFilesRepository;
    private final BoardRepository boardRepository;

    public Map<String, String> getPresignedUrl(Long boardIdx, String filesType, String filesName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));
        String key = datePath + UUID.randomUUID() + "_" + filesName;
        String contentType = determineContentType(filesName);
        String presignedUrl = preSignedUrlService.generatePreSignedUrl(key, contentType);
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("finalUrl", key);
        return response;
    }

    public void saveFileRecord(BoardFilesRequestDto dto) {
        Board board = boardRepository.findById(dto.getBoardIdx())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        BoardFiles file = BoardFiles.builder()
                .board(board)
                .filesName(dto.getFilesName())
                .filesUrl(dto.getFilesUrl())
                .filesType(dto.getFilesType())
                .build();
        boardFilesRepository.save(file);
    }

    public void deleteS3File(String key) {
        s3Service.deleteFile(key);
    }


    public void linkTempImages(Long boardIdx, String identifier) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        List<BoardFiles> tempImages = boardFilesRepository
                .findByBoardIsNullAndFilesTypeAndFilesUrlContaining("image", identifier);

        for (BoardFiles file : tempImages) {
            file.linkToBoard(board); // setter 대신
        }
        boardFilesRepository.saveAll(tempImages);
    }

    public Map<String, Object> uploadTempImage(MultipartFile file) {
        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));

            // 🔥 브라우저가 제공한 MIME type
            String contentType = file.getContentType();
            System.out.println("🔥 실제 contentType from MultipartFile: " + contentType);

            // 📌 예외 상황 대비: null 또는 알 수 없는 타입이면 확장자 기반으로 유추
            if (contentType == null || contentType.equals("application/octet-stream")) {
                contentType = determineContentType(file.getOriginalFilename());
                System.out.println("📌 Content-Type fallback to: " + contentType);
            }

            // 📎 Content-Type 기반으로 확장자 결정
            String fileExtension = switch (contentType) {
                case "image/png" -> ".png";
                case "image/jpeg" -> ".jpeg";
                case "image/gif" -> ".gif";
                default -> ""; // 예외 확장자 처리
            };

            // 🧷 _blob 제거 + 확장자 기반 키 생성
            String newFileKey = datePath + UUID.randomUUID() + fileExtension;
            System.out.println("📁 최종 업로드 키: " + newFileKey);

            // 🔐 프리사인드 URL 생성 및 업로드
            String presignedUrl = preSignedUrlService.generatePreSignedUrl(newFileKey, contentType);
            System.out.println("📎 Generated Presigned URL: " + presignedUrl);

            s3Service.uploadFileWithPresignedUrl(presignedUrl, file, contentType);

            // 📦 클라이언트 응답
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", newFileKey);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Temporary image upload failed", e);
        }
    }


    private String determineContentType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".png")) return "image/png";
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerName.endsWith(".gif")) return "image/gif";
        if (lowerName.endsWith(".pdf")) return "application/pdf";
        if (lowerName.endsWith(".doc")) return "application/msword";
        if (lowerName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lowerName.endsWith(".txt")) return "text/plain";
        return "application/octet-stream";
    }

    /**
     * 게시글과 연결할 파일 목록에 대해 S3 업로드 전 pre-signed URL을 생성합니다.
     *
     * @param board 게시글 엔티티
     * @param originalFileNames 클라이언트에서 전달받은 첨부파일의 원본 파일명 목록
     * @return S3 업로드를 위한 pre-signed URL 목록 (이후 프론트엔드에 전달되어 실제 업로드가 이루어질 수 있음)
     */
    public List<String> processFilesForBoard(Board board, List<String> originalFileNames) {
        List<String> preSignedUrls = new ArrayList<>();

        // 현재 날짜 기반 경로 생성 (예: "2025/03/05/")
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));

        for (String originalFileName : originalFileNames) {
            // 새 파일 키 생성: 날짜 경로 + UUID + "_" + 원본 파일명
            String newFileKey = datePath + UUID.randomUUID() + "_" + originalFileName;

            // 동적으로 Content-Type 결정 (확장자에 따라)
            String lowerName = originalFileName.toLowerCase();
            String contentType;
            if (lowerName.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lowerName.endsWith(".doc")) {
                contentType = "application/msword";
            } else if (lowerName.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else {
                contentType = "application/octet-stream";
            }

            // PreSigned URL 생성
            String presignedUrl = preSignedUrlService.generatePreSignedUrl(newFileKey, contentType);
            preSignedUrls.add(presignedUrl);

            // 생성된 파일 키(newFileKey)를 사용해 BoardFiles 엔티티 생성 및 저장
            BoardFiles boardFile = BoardFiles.builder()
                    .board(board)
                    .filesName(originalFileName)  // 원본 파일명 저장 (필요한 경우)
                    .filesUrl(newFileKey)           // 실제 S3에 저장될 파일 키
                    // filesType 등 다른 필드가 있다면 추가 처리
                    .build();
            boardFilesRepository.save(boardFile);
        }

        return preSignedUrls;
    }
}
