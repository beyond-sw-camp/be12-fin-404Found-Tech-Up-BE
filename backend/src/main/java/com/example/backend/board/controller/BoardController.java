// BoardController.java
package com.example.backend.board.controller;

import com.example.backend.board.model.dto.BoardPageResponse;
import com.example.backend.board.model.dto.BoardRegisterRequestDto;
import com.example.backend.board.model.dto.BoardRegisterResponseDto;
import com.example.backend.board.model.dto.BoardResponseDto;
import com.example.backend.board.service.BoardService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.user.model.User;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
@Tag(name = "게시판 기능", description = "게시판 관리 API")
public class BoardController {

    private final BoardService boardService;
    private final BaseResponseService baseResponseService;
    Logger log = LoggerFactory.getLogger(BoardController.class);

    @Operation(
            summary = "게시글 등록",
            description = "제목, 내용, 첨부파일과 함께 글을 작성합니다."
    )
    @PostMapping("/create")
    public BaseResponse<Object> create(
            @AuthenticationPrincipal User loginUser,
            @RequestBody BoardRegisterRequestDto dto) {

        System.out.println("게시글 등록 요청, 제목: " + dto.getBoardTitle());
        BoardRegisterResponseDto response = boardService.create(loginUser, dto);
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.CREATED);
    }

    @Operation(
            summary = "게시글 수정",
            description = "boardIdx를 전달받아 본인이 작성한 글인지 확인 후, 게시글의 제목과 내용, 첨부파일을 수정합니다."
    )
    @PatchMapping("/update/{boardIdx}")
    public void update(@AuthenticationPrincipal User loginUser,
                       @PathVariable Long boardIdx,
                       @RequestBody BoardRegisterRequestDto dto) {
        boardService.update(loginUser, boardIdx, dto);
    }

    @Operation(
            summary = "게시글 삭제",
            description = "boardIdx를 전달받아 본인이 작성한 글인지 확인 후, 해당 게시글을 삭제합니다."
    )
    @DeleteMapping("/delete/{boardIdx}")
    public BaseResponse<Object> delete(@AuthenticationPrincipal User loginUser,
                       @PathVariable Long boardIdx) {
        boardService.delete(loginUser, boardIdx);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.DELETED);
    }

    @Operation(
            summary = "게시글 리스트보기",
            description = "게시글 목록을 확인합니다."
    )
    @GetMapping("/list")
    public BaseResponse<Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "boardCreated") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type
    ) {
        System.out.println("sort: " + sort);
        BoardPageResponse response = boardService.getBoardList(
                page, size, sort, direction, category, search, type
        );
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.SUCCESS);
    }


    @Operation(
            summary = "게시글 상세보기",
            description = "boardIdx를 전달받아 게시글 하나의 정보를 확인합니다."
    )
    @GetMapping("/read/{boardIdx}")
    public BaseResponse<Object> read(@PathVariable Long boardIdx) {
        BoardResponseDto response = boardService.read(boardIdx);
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "내가 쓴 게시글 목록",
            description = "로그인한 사용자가 작성한 게시글을 페이징 조회합니다."
    )
    @GetMapping("/my-list")
    public BaseResponse<Object> myList(
            @AuthenticationPrincipal User loginUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BoardPageResponse response = boardService.getMyBoardList(loginUser, page, size);
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.SUCCESS);
    }
}
