package me.son.chatlabapi.user.dto;

import jakarta.validation.constraints.NotBlank;

import me.son.chatlabapi.user.domain.entity.enums.Role;

public record UserSignUpRequest(
        @NotBlank(message = "아이디는 필수입니다.") String username,
        @NotBlank(message = "비밀번호는 필수입니다.") String password,
        Role role
) {
    public UserSignUpRequest {
        if (role == null) {
            role = Role.ROLE_USER;
        }
    }
}
