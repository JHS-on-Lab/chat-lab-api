package me.son.chatlabapi.global.exception;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "요청 값이 올바르지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    GlobalErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
