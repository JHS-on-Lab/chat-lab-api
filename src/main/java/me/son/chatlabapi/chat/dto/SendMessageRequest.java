package me.son.chatlabapi.chat.dto;

public record SendMessageRequest(
        String type,
        String content
) {
}
