package me.son.chatlabapi.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank(message = "메시지 타입은 필수입니다.") String type,
        @NotBlank(message = "메시지 내용은 필수입니다.") String content
) {
}
