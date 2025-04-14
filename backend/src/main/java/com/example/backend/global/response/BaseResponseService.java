package com.example.backend.global.response;

import com.example.backend.global.response.responseStatus.BaseResponseStatus;

/**
 * BaseResponse를 반환하는 서비스 인터페이스
 */
public interface BaseResponseService {

    /**
     * 성공 응답 - 데이터 포함
     *
     * @param data 반환할 데이터
     * @param status 응답 상태 (기본값: SUCCESS)
     * @return BaseResponse
     */
    <T> BaseResponse<T> getSuccessResponse(T data, BaseResponseStatus status);

    /**
     * 성공 응답 - 데이터 없음
     *
     * @param status 응답 상태 (기본값: SUCCESS)
     * @return BaseResponse
     */
    BaseResponse<Object> getSuccessResponse(BaseResponseStatus status);

    /**
     * 실패 응답
     *
     * @param status 실패 상태 코드
     * @return BaseResponse
     */
    <T> BaseResponse<T> getFailureResponse(BaseResponseStatus status);
}
