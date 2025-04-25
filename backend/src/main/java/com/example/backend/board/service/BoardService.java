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
import com.example.backend.util.HtmlSanitizer;
import jakarta.persistence.EntityNotFoundException;
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
        System.out.println("정제 전 : " + boardRequestDto.getBoardContent());
        System.out.println("정제 후 : " + HtmlSanitizer.sanitize(boardRequestDto.getBoardContent()));
        // 게시글 엔티티 생성 및 저장
        Board board = boardRepository.save(boardRequestDto.toEntity(loginUser));

        // BoardFilesService를 통해 전달받은 파일 목록에 대해 S3 업로드 처리 및 pre-signed URL 목록 생성
        List<String> preSignedUrls = boardFilesService.processFilesForBoard(board, boardRequestDto.getFiles());

        // BoardRegisterResponseDto에 매핑하여 반환 (필요한 추가 필드는 DTO 에서 처리)
        return BoardRegisterResponseDto.from(board, preSignedUrls);
    }

    @Transactional
    public void delete(User loginUser, Long boardIdx) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 작성자 검증
        if (!board.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        // 첨부파일 삭제 (board_files 기준)
        List<String> fileKeys = board.getImageList().stream()
                .map(file -> extractS3KeyFromUrl(file.getFilesUrl()))
                .toList();
        s3Service.deleteFiles(fileKeys);

        // quill editor 내 이미지 삭제
        List<String> quillKeys = extractImageKeysFromContent(board.getBoardContent());
        s3Service.deleteFiles(quillKeys);

        // 게시글 삭제 (연관 파일은 cascade로 제거되어야 함)
        boardRepository.delete(board);
    }


    public BoardPageResponse getBoardList(int page,
                                          int size,
                                          String sort,
                                          String direction,
                                          String category,
                                          String search,
                                          String type) {


        // sort 필드와 direction (asc/desc) 에 따른 Sort 객체 생성
        Sort sorting = direction.equalsIgnoreCase("asc")
                ? Sort.by(sort).ascending()
                : Sort.by(sort).descending();

        Pageable pageable = PageRequest.of(page, size, sorting);

        // Repository 로 위 조건 모두 전달
        Page<Board> boardPage = boardRepository.searchBoards(
                category, search, type, pageable
        );

        // 정렬 필드·방향을 response 에도 담아줄 수 있습니다.
        return BoardPageResponse.from(boardPage, sort, direction);
    }




    @Transactional
    public void update(User loginUser, Long boardIdx, BoardRegisterRequestDto dto) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        if (!board.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new IllegalArgumentException("수정 권한 없음");
        }

        // 🔐 수정 시에도 XSS 방지 필터링
        String sanitizedContent = HtmlSanitizer.sanitize(dto.getBoardContent());

        // 🔄 기존 내용 갱신
        board.setBoardTitle(dto.getBoardTitle());
        board.setBoardContent(sanitizedContent);
        board.setBoardCategory(dto.getBoardCategory());

        // ✅ 1) 첨부파일 갱신
        List<String> newFiles = dto.getFiles(); // 새로 첨부된 파일 key 목록
        List<String> oldFiles = board.getImageList().stream()
                .map(f -> f.getFilesUrl()).toList();

        // 기존 파일 중 제거된 파일 삭제
        List<String> filesToDelete = oldFiles.stream()
                .filter(f -> !newFiles.contains(f))
                .collect(Collectors.toList());
        s3Service.deleteFiles(filesToDelete);

        // 새 파일을 DB에 저장 (기존 로직 재사용)
        boardFilesService.processFilesForBoard(board, newFiles);

        boardRepository.save(board);
    }



    public BoardResponseDto read(Long tempIdx) {
        Board board = boardRepository.findById(tempIdx).orElseThrow();
        return BoardResponseDto.from(board);
    }


    public BoardPageResponse getMyBoardList(User loginUser, int page, int size) {
        // 명시적 타입 사용
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardCreated").descending());
        Page<Board> boardPage = boardRepository.findAllByUser(loginUser, pageable);
        return BoardPageResponse.from(boardPage, "boardCreated", "desc");
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
