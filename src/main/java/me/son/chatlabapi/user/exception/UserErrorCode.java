package me.son.chatlabapi.user.exception;

import me.son.chatlabapi.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 사용자입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "USER_DUPLICATE_USERNAME", "이미 사용 중인 아이디 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_INVALID_PASSWORD", "현재 비밀번호가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    UserErrorCode(HttpStatus status, String code, String message) {
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
