package com.togedog.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.togedog.errorResponse.ErrorResponse;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.CacheOperationException;
import com.togedog.exception.ExceptionCode;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity handleBusinessLogicException(BusinessLogicException e) {
        ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getExceptionCode().getStatusCode()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final ErrorResponse response = ErrorResponse.of(e.getBindingResult());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        final ErrorResponse response = ErrorResponse.of(e.getConstraintViolations());
        return response;
    }

    //  Redis 연결 실패 예외 처리
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        return ErrorResponse.of(ExceptionCode.valueOf("Redis 서버에 연결할 수 없습니다. 관리자에게 문의하세요."));
    }

    // JSON 직렬화/역직렬화 오류 처리
    @ExceptionHandler({JsonProcessingException.class, IOException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleJsonProcessingException(Exception e) {
        return ErrorResponse.of(ExceptionCode.valueOf("데이터 변환 중 오류가 발생했습니다."));
    }

    //  CacheOperationException 예외 처리 (Redis 캐싱 관련 예외)
    @ExceptionHandler(CacheOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleCacheOperationException(CacheOperationException e) {
        return ErrorResponse.of(ExceptionCode.valueOf("❌ Redis 캐시 작업 중 오류 발생: " + e.getMessage()));
    }
}
