package me.son.chatlabapi.user.dto;

import me.son.chatlabapi.user.domain.entity.User;

public record UserModifyResponseDto (String username) {
    public static UserModifyResponseDto from(User user) {
        return new UserModifyResponseDto(user.getUsername());
    }
}
