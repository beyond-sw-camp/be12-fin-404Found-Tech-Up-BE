package com.example.backend.board.controller;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.BoardFiles;
import com.example.backend.board.model.dto.BoardFilesRequestDto;
import com.example.backend.board.repository.BoardFilesRepository;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.common.s3.PreSignedUrlService;
import com.example.backend.common.s3.S3Service;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.BaseResponseStatus;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/files")
public class BoardFileController {

    private final PreSignedUrlService preSignedUrlService;
    private final BoardFilesRepository boardFilesRepository;
    private final BoardRepository boardRepository;
    private final S3Service s3Service;
    private final BaseResponseService baseResponseService;

    @GetMapping("/presignedUrl")
    public Map<String, String> getPresignedUrl(
            @RequestParam("board_idx") Long boardIdx,
            @RequestParam("files_type") String filesType,
            @RequestParam("files_name") String filesName) {

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));
        String key = datePath + UUID.randomUUID() + "_" + filesName;

        String contentType = determineContentType(filesName);
        String presignedUrl = preSignedUrlService.generatePreSignedUrl(key, contentType);

        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("finalUrl", key); // 실제 저장된 S3 경로

        return response;
    }

    @PostMapping
    public BaseResponse<Object> saveFileRecord(@RequestBody BoardFilesRequestDto dto) {
        Board board = boardRepository.findById(dto.getBoardIdx())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        BoardFiles file = BoardFiles.builder()
                .board(board)
                .filesName(dto.getFilesName())
                .filesUrl(dto.getFilesUrl())
                .filesType(dto.getFilesType())
                .build();

        boardFilesRepository.save(file);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.SUCCESS);
    }


    @DeleteMapping("/s3")
    public BaseResponse<Object> deleteS3File(@RequestParam("key") String key) {
        s3Service.deleteFile(key);
        return baseResponseService.getSuccessResponse(key ,CommonResponseStatus.DELETED);
    }

    private String determineContentType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".png")) return "image/png";
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerName.endsWith(".gif")) return "image/gif";
        if (lowerName.endsWith(".pdf")) return "application/pdf";
        if (lowerName.endsWith(".doc")) return "application/msword";
        if (lowerName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }
}

