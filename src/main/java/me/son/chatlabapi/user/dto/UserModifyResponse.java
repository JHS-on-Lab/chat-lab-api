package me.son.chatlabapi.user.dto;

import me.son.chatlabapi.user.domain.entity.User;

public record UserModifyResponse(String username) {
    public static UserModifyResponse from(User user) {
        return new UserModifyResponse(user.getUsername());
    }
}
