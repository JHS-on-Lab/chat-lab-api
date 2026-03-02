package me.son.chatlabapi.user.dto;

import me.son.chatlabapi.user.domain.entity.enums.Role;

public record UserSignUpRequest(
        String username,
        String password,
        Role role
) {
    public UserSignUpRequest {
        if (role == null) {
            role = Role.ROLE_USER;
        }
    }
}
