package me.son.chatlabapi.game.exception;

import me.son.chatlabapi.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum GameErrorCode implements ErrorCode {
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME_NOT_FOUND", "존재하지 않는 게임입니다."),
    GAME_INACTIVE(HttpStatus.FORBIDDEN, "GAME_INACTIVE", "비활성화 상태인 게임입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    GameErrorCode(HttpStatus status, String code, String message) {
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
