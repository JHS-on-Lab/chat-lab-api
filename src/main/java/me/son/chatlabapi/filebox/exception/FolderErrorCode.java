package me.son.chatlabapi.filebox.exception;

import me.son.chatlabapi.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum FolderErrorCode implements ErrorCode {
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER_NOT_FOUND", "존재하지 않는 폴더입니다."),
    INVALID_FOLDER_NAME(HttpStatus.BAD_REQUEST, "INVALID_FOLDER_NAME", "폴더 이름은 비어 있을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    FolderErrorCode(HttpStatus status, String code, String message) {
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
