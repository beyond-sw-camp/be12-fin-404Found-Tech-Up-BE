// BoardFilesController.java
package com.example.backend.board.controller;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.BoardFiles;
import com.example.backend.board.model.dto.BoardFilesRequestDto;
import com.example.backend.board.repository.BoardFilesRepository;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.board.service.BoardFilesService;
import com.example.backend.common.s3.PreSignedUrlService;
import com.example.backend.common.s3.S3Service;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/files")
public class BoardFilesController {

    private final BoardFilesService boardFilesService;
    private final BaseResponseService baseResponseService;

    /**
     * 프리사인드 URL 발급 엔드포인트
     */
    @GetMapping("/presignedUrl")
    public Map<String, String> getPresignedUrl(
            @RequestParam("board_idx") Long boardIdx,
            @RequestParam("files_type") String filesType,
            @RequestParam("files_name") String filesName) {

        return boardFilesService.getPresignedUrl(boardIdx, filesType, filesName);
    }

    /**
     * 파일 정보 저장 엔드포인트
     */
    @PostMapping
    public BaseResponse<Object> saveFileRecord(@RequestBody BoardFilesRequestDto dto) {
        boardFilesService.saveFileRecord(dto);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.SUCCESS);
    }

    /**
     * S3 파일 삭제 엔드포인트
     */
    @DeleteMapping("/s3")
    public BaseResponse<Object> deleteS3File(@RequestParam("key") String key) {
        boardFilesService.deleteS3File(key);
        return baseResponseService.getSuccessResponse(key, CommonResponseStatus.DELETED);
    }

    /**
     * 임시 이미지들을 게시글과 연결하는 엔드포인트.
     */
    @PostMapping("/{boardIdx}/link-temp-images")
    public BaseResponse<Object> linkTempImages(
            @PathVariable Long boardIdx,
            @RequestParam String identifier) {
        boardFilesService.linkTempImages(boardIdx, identifier);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.SUCCESS);
    }


    /**
     * 임시 이미지 업로드 엔드포인트.
     */
    @PostMapping("/temp-image")
    public BaseResponse<Map<String, Object>> uploadTempImage(@RequestParam("file") MultipartFile file) {
        System.out.println("받은 이미지 : " + file.getOriginalFilename());
        Map<String, Object> response = boardFilesService.uploadTempImage(file);
        System.out.println("보내는 이미지 : " + response);
        System.out.println("보내는 이미지 : " + response.get("imageUrl"));
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.CREATED);
    }
}
