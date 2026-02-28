package me.son.chatlabapi.chat.exception;

import me.son.chatlabapi.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ChatRoomErrorCode implements ErrorCode {
    INVALID_ROOM_NAME(HttpStatus.BAD_REQUEST, "CHATROOM_INVALID_NAME", "채팅방 이름이 유효하지 않습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM_NOT_FOUND", "채팅방을 찾을 수 없습니다."),
    NOT_A_ROOM_MEMBER(HttpStatus.FORBIDDEN, "CHATROOM_NOT_MEMBER", "해당 채팅방의 참가자가 아닙니다."),
    ALREADY_ROOM_MEMBER(HttpStatus.CONFLICT,"CHATROOM_ALREADY_MEMBER","이미 채팅방에 참여 중입니다." ),
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
