package me.son.chatlabapi.global.exception;

import lombok.extern.slf4j.Slf4j;

import me.son.chatlabapi.auth.jwt.exception.CustomJwtException;
import me.son.chatlabapi.global.response.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(GlobalErrorCode.VALIDATION_FAILED.getMessage());

        return ResponseEntity
                .status(GlobalErrorCode.VALIDATION_FAILED.getStatus())
                .body(ApiResponse.failure(GlobalErrorCode.VALIDATION_FAILED.getCode(), message));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(CustomJwtException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled exception", e);

        return ResponseEntity
                .status(500)
                .body(ApiResponse.failure(GlobalErrorCode.INTERNAL_SERVER_ERROR));
    }
}
