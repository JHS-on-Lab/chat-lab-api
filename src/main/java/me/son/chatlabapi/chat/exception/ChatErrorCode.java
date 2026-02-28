package me.son.chatlabapi.chat.exception;

import me.son.chatlabapi.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ChatErrorCode implements ErrorCode {
    INVALID_ROOM_NAME(HttpStatus.BAD_REQUEST, "CHAT_INVALID_ROOM_NAME", "채팅방 이름이 유효하지 않습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM_NOT_FOUND", "채팅방을 찾을 수 없습니다."),
    NOT_ROOM_MEMBER(HttpStatus.FORBIDDEN, "CHAT_NOT_ROOM_MEMBER", "해당 채팅방의 참가자가 아닙니다."),
    ALREADY_ROOM_MEMBER(HttpStatus.CONFLICT, "CHAT_ALREADY_ROOM_MEMBER", "이미 채팅방에 참여 중입니다."),
    INVALID_MESSAGE_TYPE(HttpStatus.BAD_REQUEST, "CHAT_INVALID_MESSAGE_TYPE", "메시지 타입이 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ChatErrorCode(HttpStatus status, String code, String message) {
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
