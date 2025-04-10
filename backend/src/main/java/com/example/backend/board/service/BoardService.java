package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.dto.BoardRegisterRequestDto;
import com.example.backend.board.model.dto.BoardRegisterResponseDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.repository.BoardImageRepository;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.common.s3.PreSignedUrlService;
import com.example.backend.common.s3.S3Service;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final PreSignedUrlService preSignedUrlService;
    private final S3Service s3Service;

    public BoardRegisterResponseDto create(User loginUser, BoardRegisterRequestDto boardRequestDto, Long boardType) {
        // boardRequestDto가 null인 경우 예외 발생 (예외 처리 로직은 추후 추가)
        if (boardRequestDto == null) {
            throw new IllegalArgumentException("BoardRequestDto is null");
        }

        // 게시글 엔티티 생성 및 저장 (BoardRequestDto.toEntity() 메소드 내에서 boardType, loginUser 활용)
        Board board = boardRepository.save(boardRequestDto.toEntity(loginUser, boardType));

        // 파일 업로드 시 S3에 저장할 키와 프리사인드 URL들을 담을 리스트
        List<String> fileKeys = new ArrayList<>();
        List<String> preSignedUrls = new ArrayList<>();

        // boardRequestDto의 files() 메소드로 전달받은 원본 파일명 목록 처리
        for (String originalFileName : boardRequestDto.getFiles()) {
            // 현재 날짜 기반 경로 생성 (예: 2025/03/05/)
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));
            // 새 파일 키 생성: 날짜 경로 + UUID + "_" + 원본 파일명
            String newFileKey = datePath + UUID.randomUUID() + "_" + originalFileName;

            // 프리사인드 URL 생성 (contentType은 "image/png"로 고정되어 있음; 필요 시 동적으로 변경)
            String preSignedUrl = preSignedUrlService.generatePreSignedUrl(newFileKey, "image/png");
            preSignedUrls.add(preSignedUrl);
            fileKeys.add(newFileKey);
        }

        // 파일 키 목록에 해당하는 이미지들을 board와 연관지어 저장 (boardImageRepository.saveAllImages() 메소드 내부 구현에 따라 처리)
        boardImageRepository.saveAllImages(fileKeys, board);

        // preSignedUrls 목록과 함께 등록된 board 정보를 BoardResponseDto로 변환하여 반환
        return BoardRegisterResponseDto.from(board, preSignedUrls);
    }


    public void update(User loginUser, Long boardIdx, BoardRegisterRequestDto dto) {
        // 1. 기존 엔티티 조회
        Board board = boardRepository.findById(boardIdx).orElseThrow();

        if (board.getUser().equals(loginUser)) {
            // 2. DTO와 비교 후 변경된 필드만 엔티티에 반영
            if (dto.getBoardTitle() != null && !dto.getBoardTitle().equals(board.getBoardTitle())) {
                board.setBoardTitle(dto.getBoardTitle());
            }
            if (dto.getBoardContent() != null && !dto.getBoardContent().equals(board.getBoardContent())) {
                board.setBoardContent(dto.getBoardContent());
            }
//            if (dto.getFile() != null) { // 첨부파일은 null 체크만으로 충분
//                String newFilePath = fileStorageService.upload(dto.getFile());
//                fileStorageService.delete(board.getFilePath()); // 기존 파일 삭제
//                board.setFilePath(newFilePath);
//            }

            // 3. 저장 (변경 감지에 의해 업데이트 쿼리 실행)
            boardRepository.save(board);
        }
    }

    public void delete(User loginUser, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx).orElseThrow();

        // 어드민에서 삭제 가능하게 하려면 유저가 admin인지 확인하는 or 조건문 추가 필요
        if (board.getUser().equals(loginUser)) {
//            List<String> fileUrls = boardImageRepository.findUrlsByBoard(board);
//
//            if (!fileUrls.isEmpty()) {
//                s3Service.deleteFiles(fileUrls);
//            }
//
//            boardImageRepository.deleteByBoard(board);

            boardRepository.delete(board);
        }
    }

    public List<BoardResponseDto> list() {
        List<Board> result = boardRepository.findAll();

        return result.stream().map(BoardResponseDto::from).toList();
    }

    public BoardResponseDto read(Long tempIdx) {
        Board board = boardRepository.findById(tempIdx).orElseThrow();
        return BoardResponseDto.from(board);
    }
}
