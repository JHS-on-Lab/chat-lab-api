package me.son.chatlabapi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserModifyRequest(
        @NotBlank(message = "아이디는 필수입니다.") String username,
        @NotBlank(message = "비밀번호는 필수입니다.") String password

) {
}
