package com.example.backend.board.service;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.dto.BoardRegisterDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.repository.BoardImageRepository;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
//    private final S3Service s3Service;

    public void create(User loginUser, BoardRegisterDto dto) {
        boardRepository.save(dto.toEntity(loginUser));
    }

    public void update(User loginUser, Long boardIdx, BoardRegisterDto dto) {
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
