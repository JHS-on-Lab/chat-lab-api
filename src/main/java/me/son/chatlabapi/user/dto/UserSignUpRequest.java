package me.son.chatlabapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import me.son.chatlabapi.user.domain.entity.enums.Role;

public record UserSignUpRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(max = 50, message = "아이디는 50자 이하여야 합니다.") String username,
        // BCrypt 는 72바이트까지만 처리하므로 상한을 둔다
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(max = 72, message = "비밀번호는 72자 이하여야 합니다.") String password,
        Role role
) {
    public UserSignUpRequest {
        if (role == null) {
            role = Role.ROLE_USER;
        }
    }
}
