package me.son.chatlabapi.user.dto;

import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.entity.enums.Role;

import java.time.LocalDateTime;

public record UserSignUpResponse(
        Long id,
        String username,
        Role role,
        LocalDateTime createdAt
) {

    public static UserSignUpResponse from(User user) {
        return new UserSignUpResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
