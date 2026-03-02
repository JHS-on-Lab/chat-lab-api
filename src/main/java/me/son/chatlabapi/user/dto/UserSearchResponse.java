package me.son.chatlabapi.user.dto;

import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.entity.enums.Role;

import java.time.LocalDateTime;

public record UserSearchResponse(
        Long id,
        String username,
        Role role,
        LocalDateTime createdAt
) {

    public static UserSearchResponse from(User user) {
        return new UserSearchResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
