package me.son.chatlabapi.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record InviteMemberRequest(
        @NotBlank(message = "초대할 아이디는 필수입니다.") String username
) {
}
