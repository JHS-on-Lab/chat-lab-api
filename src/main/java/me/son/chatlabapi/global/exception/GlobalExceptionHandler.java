package me.son.chatlabapi.global.exception;

import lombok.extern.slf4j.Slf4j;

import me.son.chatlabapi.auth.jwt.exception.CustomJwtException;
import me.son.chatlabapi.auth.jwt.exception.JwtErrorCode;
import me.son.chatlabapi.global.response.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(GlobalErrorCode.VALIDATION_FAILED.getMessage());

        return toResponse(GlobalErrorCode.VALIDATION_FAILED, message);
    }

    // 잘못된 JSON 본문 / 타입이 맞지 않는 경로·쿼리 파라미터 / 필수 파라미터 누락 → 400
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiResponse<?>> handleBadRequest(Exception e) {
        return toResponse(GlobalErrorCode.VALIDATION_FAILED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NoResourceFoundException e) {
        return toResponse(GlobalErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return toResponse(GlobalErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        return toResponse(e.getErrorCode());
    }

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(CustomJwtException e) {
        return toResponse(e.getErrorCode());
    }

    // Refresh Token 쿠키 누락 → 토큰 없음으로 응답 (클라이언트가 로그아웃 처리)
    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingCookieException(MissingRequestCookieException e) {
        return toResponse(JwtErrorCode.JWT_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return toResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<?>> toResponse(ErrorCode errorCode) {
        return toResponse(errorCode, errorCode.getMessage());
    }

    private ResponseEntity<ApiResponse<?>> toResponse(ErrorCode errorCode, String message) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), message));
    }
}
