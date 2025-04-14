package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum CommentResponseStatus implements BaseResponseStatus {
    // 7000번대 - Comment 관련 에러
    COMMENT_NOT_FOUND(false, 7001, "댓글을 찾을 수 없습니다."),
    INVALID_BOARD_ID(false, 7002, "댓글을 작성할 수 없는 게시글 입니다.."),
    INVALID_COMMENT_ID(false, 7003, "잘못된 댓글 ID입니다."),
    COMMENT_CREATION_FAILED(false, 7004, "댓글 작성에 실패했습니다."),
    COMMENT_UPDATE_FAILED(false, 7005, "댓글 수정에 실패했습니다."),
    COMMENT_DELETE_FAILED(false, 7006, "댓글 삭제에 실패했습니다."),
    COMMENT_ACCESS_DENIED(false, 7007, "댓글에 접근할 권한이 없습니다."),
    COMMENT_TOO_LONG(false, 7008, "댓글이 너무 깁니다."),
    COMMENT_EMPTY(false, 7009, "댓글 내용이 비어 있습니다."),
    COMMENT_RATE_LIMIT_EXCEEDED(false, 7010, "댓글 작성 제한 횟수를 초과했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    CommentResponseStatus(boolean isSuccess, int code, String message) {
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
