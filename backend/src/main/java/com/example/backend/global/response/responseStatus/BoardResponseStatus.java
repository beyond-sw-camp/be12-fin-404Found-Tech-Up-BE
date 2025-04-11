package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum BoardResponseStatus implements BaseResponseStatus {
    // 6000번대 - Board 관련 에러
    BOARD_TYPE_NOT_FOUND(false, 6001, "게시판 정보를 찾을 수 없습니다."),
    BOARD_NOT_FOUND(false, 6002, "게시글을 찾을 수 없습니다."),
    DUPLICATE_BOARD(false, 6003, "이미 존재하는 게시글입니다."),
    INVALID_BOARD_ID(false, 6004, "잘못된 게시글 ID입니다."),
    COMMENT_NOT_FOUND(false, 6005, "댓글을 찾을 수 없습니다."),
    DUPLICATE_COMMENT(false, 6006, "이미 존재하는 댓글입니다."),
    INVALID_COMMENT_ID(false, 6007, "잘못된 댓글 ID입니다."),
    BOARD_CREATION_FAILED(false, 6008, "게시글 작성에 실패했습니다."),
    COMMENT_CREATION_FAILED(false, 6009, "댓글 작성에 실패했습니다."),
    BOARD_ACCESS_DENIED(false, 6010, "게시글에 접근할 권한이 없습니다."),
    COMMENT_ACCESS_DENIED(false, 6011, "댓글에 접근할 권한이 없습니다."),
    INVALID_PAGE(false, 6012, "잘못된 페이지 접근 입니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    BoardResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
