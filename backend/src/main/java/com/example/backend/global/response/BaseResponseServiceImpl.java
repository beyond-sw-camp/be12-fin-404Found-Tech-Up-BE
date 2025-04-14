package com.example.backend.global.response;

import com.example.backend.global.response.responseStatus.BaseResponseStatus;
import org.springframework.stereotype.Service;

/**
 * BaseResponseService 구현체
 */
@Service
public class BaseResponseServiceImpl implements BaseResponseService {

    /**
     * 성공 응답 - 데이터 포함
     *
     * @param data 반환할 데이터
     * @param status 응답 상태
     * @return BaseResponse
     */
    @Override
    public <T> BaseResponse<T> getSuccessResponse(T data, BaseResponseStatus status) {
        return new BaseResponse<>(true,  status.getMessage(), status.getCode(), data);
    }

    /**
     * 성공 응답 - 데이터 없음
     *
     * @param status 응답 상태
     * @return BaseResponse
     */
    @Override
    public BaseResponse<Object> getSuccessResponse(BaseResponseStatus status) {
        return new BaseResponse<>(true,  status.getMessage(), status.getCode(),null);
    }



    /**
     * 실패 응답
     *
     * @param status 실패 상태 코드
     * @return BaseResponse
     */

    @Override
    public <T> BaseResponse<T> getFailureResponse(BaseResponseStatus status) {
        return new BaseResponse<>(false,  status.getMessage(), status.getCode(),null);
    }
}
