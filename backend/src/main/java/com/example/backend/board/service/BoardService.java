package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.BoardFiles;
import com.example.backend.board.model.dto.BoardRegisterRequestDto;
import com.example.backend.board.model.dto.BoardRegisterResponseDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.repository.BoardFilesRepository;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFilesService boardFilesService;
    /**
     * 게시글 등록 메서드.
     * - BoardRegisterRequestDto 로부터 Board 엔티티 생성 후 저장.
     * - 첨부파일 원본 파일명 목록을 받아 S3 업로드를 위한 파일 키 및 pre-signed URL 생성 로직은
     *   BoardFilesService의 processFilesForBoard() 메서드에서 처리.
     * - 생성된 Board와 preSignedUrl 목록을 응답 DTO에 매핑하여 반환.
     *
     * @param loginUser 등록 요청한 사용자
     * @param boardRequestDto 게시글 등록에 필요한 정보(제목, 내용, 카테고리, 파일 목록 포함)
     * @return BoardRegisterResponseDto 응답 DTO (파일 업로드용 pre-signed URL 포함)
     */
    public BoardRegisterResponseDto create(User loginUser, BoardRegisterRequestDto boardRequestDto) {
        if (boardRequestDto == null) {
            throw new IllegalArgumentException("BoardRegisterRequestDto is null");
        }

        // 게시글 엔티티 생성 및 저장
        Board board = boardRepository.save(boardRequestDto.toEntity(loginUser));

        // BoardFilesService를 통해 전달받은 파일 목록에 대해 S3 업로드 처리 및 pre-signed URL 목록 생성
        List<String> preSignedUrls = boardFilesService.processFilesForBoard(board, boardRequestDto.getFiles());

        // BoardRegisterResponseDto에 매핑하여 반환 (필요한 추가 필드는 DTO 에서 처리)
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
