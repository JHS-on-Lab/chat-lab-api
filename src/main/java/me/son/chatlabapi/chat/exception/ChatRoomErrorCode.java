package me.son.chatlabapi.chat.exception;

import me.son.chatlabapi.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ChatRoomErrorCode implements ErrorCode {
    INVALID_ROOM_NAME(HttpStatus.NOT_FOUND, "CHATROOM_INVALID_ROOM_NAME", "유효하지 않은 채팅방 이름입니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM_NOT_FOUND", "채팅방이 존재하지 않습니다."),
    NOT_A_ROOM_MEMBER(HttpStatus.NOT_FOUND, "CHATROOM_NOT_A_ROOM_MEMBER", "채팅방 참가자가 아닙니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ChatRoomErrorCode(HttpStatus status, String code, String message) {
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
