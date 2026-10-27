package me.son.chatlabapi.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
        @NotBlank(message = "채팅방 이름은 필수입니다.")
        @Size(max = 100, message = "채팅방 이름은 100자 이하여야 합니다.") String name
) {
}
