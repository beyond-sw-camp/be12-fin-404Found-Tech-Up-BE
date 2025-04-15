package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.BoardFiles;
import com.example.backend.board.model.dto.BoardPageResponse;
import com.example.backend.board.model.dto.BoardRegisterRequestDto;
import com.example.backend.board.model.dto.BoardRegisterResponseDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.repository.BoardFilesRepository;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.common.s3.PreSignedUrlService;
import com.example.backend.common.s3.S3Service;
import com.example.backend.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFilesService boardFilesService;
    private final S3Service s3Service;
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

    @Transactional
    public void delete(User loginUser, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 작성자 검증
        if (!board.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        // 1️⃣ 첨부파일 삭제 (board_files 기준)
        List<String> fileKeys = board.getImageList().stream()
                .map(file -> extractS3KeyFromUrl(file.getFilesUrl()))
                .toList();
        s3Service.deleteFiles(fileKeys);

        // 2️⃣ quill editor 내 이미지 삭제
        List<String> quillKeys = extractImageKeysFromContent(board.getBoardContent());
        s3Service.deleteFiles(quillKeys);

        // 3️⃣ 게시글 삭제 (연관 파일은 cascade로 제거되어야 함)
        boardRepository.delete(board);
    }



    public BoardPageResponse getBoardList(int page, int size, String sort, String direction) {
        Sort sorting = direction.equalsIgnoreCase("asc")
                ? Sort.by(sort).ascending()
                : Sort.by(sort).descending();

        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Board> boardPage = boardRepository.findAll(pageable);

        return BoardPageResponse.from(boardPage, sort, direction);
    }


    public BoardResponseDto read(Long tempIdx) {
        Board board = boardRepository.findById(tempIdx).orElseThrow();
        return BoardResponseDto.from(board);
    }

    private String extractS3KeyFromUrl(String urlOrKey) {
        // 이미 key 형식이라면 그냥 반환
        if (!urlOrKey.startsWith("http")) return urlOrKey;

        try {
            URL url = new URL(urlOrKey);
            return url.getPath().substring(1); // 앞의 "/" 제거
        } catch (Exception e) {
            throw new IllegalArgumentException("S3 URL 형식 오류: " + urlOrKey);
        }
    }



    private List<String> extractImageKeysFromContent(String html) {
        Pattern pattern = Pattern.compile("https://[\\w\\-\\.]+\\.amazonaws\\.com/([\\w\\-/\\.]+)");
        Matcher matcher = pattern.matcher(html);
        List<String> keys = new ArrayList<>();

        while (matcher.find()) {
            keys.add(matcher.group(1)); // group(1)이 키
        }

        return keys;
    }


}
