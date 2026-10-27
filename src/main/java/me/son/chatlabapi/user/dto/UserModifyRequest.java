package me.son.chatlabapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserModifyRequest(
        @NotBlank(message = "현재 비밀번호는 필수입니다.") String currentPassword,
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(max = 50, message = "아이디는 50자 이하여야 합니다.") String username,
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(max = 72, message = "비밀번호는 72자 이하여야 합니다.") String password
) {
}
