package com.example.backend.global.exception;

import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.BaseResponseStatus;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *  전역 예외 핸들러
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final BaseResponseService baseResponseService;
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 커스텀 예외 (BaseException) 처리
     */

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Object>> handleBaseException(BaseException e) {
        log.error("BaseException 발생: {}", e.getStatus().getMessage());
        BaseResponseStatus status = e.getStatus();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(baseResponseService.getFailureResponse(status));
    }



    /**
     * 검증 실패 예외 처리 (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(baseResponseService.getFailureResponse(CommonResponseStatus.BAD_REQUEST));
    }

    /**
     *  요청 파라미터 누락 예외 처리 (400 Bad Request)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Object>> handleMissingParams(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(baseResponseService.getFailureResponse(CommonResponseStatus.MISSING_REQUEST_PARAMETER));
    }

    /**
     *  (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneralException(Exception e) {
        log.error("예기치 못한 예외 발생: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(baseResponseService.getFailureResponse(CommonResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
